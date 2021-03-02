#! /usr/bin/env python3

"""
This script partially parses CSPICE.java and generates
the list of function declarations it contains.
"""

import sys
import os
from antlr4 import FileStream, CommonTokenStream
from DeclarationsLexer import DeclarationsLexer
from DeclarationsParser import DeclarationsParser
import parse_tree


def main(argv):
    """
    CLI arg is the path to CSPICE.java.
    """

    input_stream = FileStream(argv[1])
    lexer = DeclarationsLexer(input_stream)
    stream = CommonTokenStream(lexer)
    parser = DeclarationsParser(stream)
    functions = parser.cspice().result

    template_dir = os.path.join('src', 'build', 'templates')
    out = os.path.join('build', 'generated', 'sources', 'automation', 'main')
    java_out = os.path.join(out, 'java')
    proto_out = os.path.join(out, 'proto')
    for o in [out, java_out, proto_out]:
        if not os.path.exists(o):
            os.makedirs(o)

    functions = [func for func in functions if valid_function(func)]

    generate_proto(functions, proto_out, template_dir)
    generate_factory(functions, java_out, template_dir)

    for func in functions:
        if func.classification == parse_tree.Classification.NORMAL:
            try:
                generate_java(func, ['Call.java', 'Batch.java', 'Future.java'], java_out, template_dir)
            except ValueError:
                print('not yet working: %s' % func.name)


def generate_factory(funcs, out, template_dir):
    """
    Generate the ParSPICE.java file from template.

    :param funcs: list of functions to generate.
    :param out: where to output the resulting file.
    :param template_dir: directory of ParSPICE.java template file.
    :return: void
    """
    factories = ''
    imports = ''
    for func in funcs:
        upper_name = func.name[0].upper() + func.name[1:]
        lower_name = func.name

        if func.classification == parse_tree.Classification.NORMAL:
            factories += """
            public %sBatch %s() {
                return new %sBatch(stub);
            }
            """ % (upper_name, lower_name, upper_name)
            imports += 'import parspice.functions.%s.%sBatch;\n' % (upper_name, upper_name)
        elif func.classification == parse_tree.Classification.CONSTANT:
            factories += """
            public %s %s() {
                return CSPICE.%s();
            }
            """ % (str(func.return_type), func.name, func.name)
        else:
            imports += 'import parspice.rpc.%sRequest;\n' % upper_name
            imports += 'import parspice.rpc.%sResponse;\n' % upper_name
            args = ''
            builders = ''
            nested_builders = ''
            getters = ''
            for i,arg in enumerate(func.args):
                cap_name = arg.name[0].upper() + arg.name[1:]
                object_type = arg.data_type.object_str()
                base_object_type = arg.data_type.base_object_str()
                args += '%s %s, ' % (object_type, arg.name)
                if arg.io == parse_tree.IO.INPUT or arg.io == parse_tree.IO.BOTH:
                    if arg.data_type.array_depth == 0:
                        builders += '.set%s(%s)\n' % (cap_name, arg.name)
                    elif arg.data_type.array_depth == 1:
                        builders += '.addAll%s(Arrays.asList(%s))\n' % (cap_name, arg.name)
                    elif arg.data_type.array_depth == 2:
                        nested_builders += """
                        ArrayList<Repeated%s> nested%i = new ArrayList<Repeated%s>();
                        for (%s[] row : %s) {
                            nested%i.add(
                                Repeated%s.newBuilder()
                                    .addAllArray(Arrays.asList(row))
                                    .build()
                            );
                        }\n
                        """ % (base_object_type, i, base_object_type, base_object_type, arg.name, i, base_object_type)
                        builders += ".addAll%s(nested%i)\n" % (cap_name, i)
                    else:
                        return
                if arg.io == parse_tree.IO.OUTPUT or arg.io == parse_tree.IO.BOTH:
                    if arg.data_type.array_depth == 1:
                        getters += 'response.get%sList().toArray(%s);\n' % (cap_name, arg.name)
                    elif arg.data_type.array_depth == 2:
                        getters += """
                        List<Repeated%s> full%i = response.get%sList();
                        for (int j = 0; j < full%i.size(); j++) {
                            full%i.get(j).getArrayList().toArray(%s[j]);
                        }\n
                        """ % (base_object_type, i, cap_name, i, i, arg.name)
                    else:
                        return
            return_line = ''
            return_type = 'void'
            if func.return_type.base_type != parse_tree.DataType.VOID:
                return_type = func.return_type.object_str()
                base_return_type = func.return_type.base_object_str()
                if func.return_type.array_depth == 0:
                    return_line = 'return response.getRet();'
                elif func.return_type.array_depth == 1:
                    return_line = """
                    %s ret = new %s[response.getRetCount()];
                    response.getRetList().toArray(ret);
                    return ret;
                    """ % (return_type, base_return_type)
                elif func.return_type.array_depth == 2:
                    return_line = """
                    List<Repeated%s> fullRet = response.getRetList();
                    %s ret = new %s[fullRet.size()][fullRet.get(0).getArrayCount()];
                    for (int j = 0; j < fullRet.size(); j++) {
                        fullRet.get(j).getArrayList().toArray(ret[j]);
                    }
                    return ret;
                    """ % (base_return_type, return_type, base_return_type)
            args = args[:-2]
            factories += ("""
            public %s ###LOWER_NAME###(###ARGS###) {
                ###NESTED_BUILDERS###
                ###UPPER_NAME###Request request = ###UPPER_NAME###Request.newBuilder()
                    ###BUILDERS###
                    .build();
                ###UPPER_NAME###Response response = blockingStub.###LOWER_NAME###RPC(request);
                ###GETTERS###
                %s
            }
            """ % (return_type, return_line)) \
                .replace('###LOWER_NAME###', func.name) \
                .replace('###UPPER_NAME###', upper_name) \
                .replace('###GETTERS###', getters) \
                .replace('###BUILDERS###', builders) \
                .replace('###ARGS###', args) \
                .replace('###NESTED_BUILDERS###', nested_builders)
    with open(os.path.join(template_dir, 'ParSPICE.java'), 'r') as template:
        parspice = template.read() \
            .replace('###FACTORIES###', factories) \
            .replace('###IMPORTS###', imports)
        with open(os.path.join(out, 'ParSPICE.java'), 'w') as out_file:
            out_file.write(parspice)



def generate_proto(funcs, out, template_dir):
    """
    Generate parspice.proto messages from template.

    :param funcs: list of functions to generate from
    :param out: output dir to place the file
    :param template_dir: where to find the template
    :return: void
    """
    services = ''
    messages = ''
    for func in funcs:
        upper_name = func.name[0].upper() + func.name[1:]

        inputs = ''
        outputs = ''

        for i,arg in enumerate(func.args):
            ty = arg.data_type.proto_str()
            if arg.io == parse_tree.IO.INPUT or arg.io == parse_tree.IO.BOTH:
                inputs += '%s %s = %i;\n' % (ty, arg.name, i+1)
            if arg.io == parse_tree.IO.OUTPUT or arg.io == parse_tree.IO.BOTH:
                outputs += '%s %s = %i;\n' % (ty, arg.name, i+1)

        if func.return_type.base_type != parse_tree.DataType.VOID:
            ty = func.return_type.proto_str()
            outputs += '%s ret = %i;\n' % (ty, len(func.args) + 1)

        outputs += 'string error = %i;\n' % (len(func.args) + 2)

        if func.classification == parse_tree.Classification.NORMAL:
            services += 'rpc ###UPPER_NAME###RPC (###UPPER_NAME###Request) returns (###UPPER_NAME###Response);\n' \
                .replace('###UPPER_NAME###', upper_name)
            messages += """
            message ###UPPER_NAME###Request {
                message ###UPPER_NAME###Input {
                    ###INPUTS###
                }
                int32 batchID = 1;
                repeated ###UPPER_NAME###Input inputs = 2;
            }
            
            message ###UPPER_NAME###Response {
                message ###UPPER_NAME###Output {
                    ###OUTPUTS###
                }
                int32 batchID = 1;
                repeated ###UPPER_NAME###Output outputs = 2;
            }
            """.replace('###UPPER_NAME###', upper_name) \
                .replace('###INPUTS###', inputs) \
                .replace('###OUTPUTS###', outputs)
        elif func.classification == parse_tree.Classification.GLOBAL_STATE_MODIFIER \
                or func.classification == parse_tree.Classification.TASK_STATEFUL:
            services += 'rpc ###UPPER_NAME###RPC (###UPPER_NAME###Request) returns (###UPPER_NAME###Response);\n' \
                .replace('###UPPER_NAME###', upper_name)
            messages += """
            message ###UPPER_NAME###Request {
                ###INPUTS###
            }
            
            message ###UPPER_NAME###Response {
                ###OUTPUTS###
            }
            """.replace('###UPPER_NAME###', upper_name) \
                .replace('###INPUTS###', inputs) \
                .replace('###OUTPUTS###', outputs)

    with open(os.path.join(template_dir, 'parspice.proto'), 'r') as template:
        proto = template.read() \
            .replace('###SERVICES###', services) \
            .replace('###MESSAGES###', messages)
        with open(os.path.join(out, 'parspice.proto'), 'w') as out_file:
            out_file.write(proto)


def generate_java(func, templates, out, template_dir):
    """
    Generate java files for a particular SPICE function.

    :param func: function to generate from
    :param templates: List of template Java files to generate from
    :param out: where to put the generated output files
    :param template_dir: directory of the template files
    :return: void
    """

    upper_name = func.name[0].upper() + func.name[1:]
    lower_name = func.name

    fields = ''
    args = ''
    args_no_types = ''
    assign_fields = ''
    builders = ''
    getters = ''
    nested_builders = ''

    for i,arg in enumerate(func.args):
        if arg.data_type.base_type == parse_tree.DataType.GFSEARCHUTILS \
            or arg.data_type.base_type == parse_tree.DataType.GFSCALARQUANTITY:
            return

        cap_name = arg.name[0].upper() + arg.name[1:]

        object_type = arg.data_type.object_str()
        base_object_type = arg.data_type.base_object_str()

        fields += 'public %s %s;\n' % (object_type, arg.name)

        if arg.io == parse_tree.IO.INPUT or arg.io == parse_tree.IO.BOTH:
            args += '%s %s, ' % (object_type, arg.name)
            args_no_types += '%s, ' % arg.name
            assign_fields += 'this.%s = %s;\n' % (arg.name, arg.name)

            if arg.data_type.array_depth == 0:
                builders += '.set%s(call.%s)\n' % (cap_name, arg.name)
            elif arg.data_type.array_depth == 1:
                builders += '.addAll%s(Arrays.asList(call.%s))\n' % (cap_name, arg.name)
            elif arg.data_type.array_depth == 2:
                nested_builders += """
                ArrayList<Repeated%s> nested%i = new ArrayList<Repeated%s>();
                for (%s[] row : call.%s) {
                    nested%i.add(
                        Repeated%s.newBuilder()
                            .addAllArray(Arrays.asList(row))
                            .build()
                    );
                }\n
                """ % (base_object_type, i, base_object_type, base_object_type, arg.name, i, base_object_type)
                builders += ".addAll%s(nested%i)\n" % (cap_name, i)
            else:
                return
        if arg.io == parse_tree.IO.OUTPUT or arg.io == parse_tree.IO.BOTH:
            if arg.data_type.array_depth == 0:
                getters += 'call.%s = output.get%s();\n' % (arg.name, cap_name)
            elif arg.data_type.array_depth == 1:
                getters += 'call.%s = new %s[output.get%sCount()];\n' % (arg.name, base_object_type, cap_name)
                getters += 'output.get%sList().toArray(call.%s);\n' % (cap_name, arg.name)
            elif arg.data_type.array_depth == 2:
                getters += 'call.%s = new %s[output.get%sCount()][output.get%s(0).getArrayCount()];\n' \
                           % (arg.name, base_object_type, cap_name, cap_name)
                getters += """
                List<Repeated%s> full%i = output.get%sList();
                for (int j = 0; j < full%i.size(); j++) {
                    full%i.get(j).getArrayList().toArray(call.%s[j]);
                }\n
                """ % (base_object_type, i, cap_name, i, i, arg.name)
            else:
                return

    args = args[:-2]
    args_no_types = args_no_types[:-2]

    if func.return_type.base_type != parse_tree.DataType.VOID:
        object_type = func.return_type.object_str()
        base_object_type = func.return_type.base_object_str()
        fields += 'public %s ret;\n' % object_type
        if func.return_type.array_depth == 0:
            getters += 'call.ret = output.getRet();\n'
        elif func.return_type.array_depth == 1:
            getters += 'call.ret = new %s[output.getRetCount()];\n' % base_object_type
            getters += 'output.getRetList().toArray(call.ret);\n'
        elif func.return_type.array_depth == 2:
            getters += 'call.ret = new %s[output.getRetCount()][output.getRet(0).getArrayCount()];\n' % base_object_type
            getters += """
                List<Repeated%s> fullRet = output.getRetList();
                for (int j = 0; j < fullRet.size(); j++) {
                    fullRet.get(j).getArrayList().toArray(call.ret[j]);
                }\n
                """ % base_object_type


    for template in templates:
        with open(os.path.join(template_dir, template), 'r') as in_file:
            output = in_file.read() \
                .replace('###LOWER_NAME###', lower_name) \
                .replace('###UPPER_NAME###', upper_name) \
                .replace('###FIELDS###', fields) \
                .replace('###ARGS###', args) \
                .replace('###ARGS_NO_TYPES###', args_no_types) \
                .replace('###ASSIGN_FIELDS###', assign_fields) \
                .replace('###BUILDERS###', builders) \
                .replace('###GETTERS###', getters) \
                .replace('###NESTED_BUILDERS###', nested_builders)
            with open(os.path.join(out, '%s%s' % (upper_name, template)), 'w') as out_file:
                out_file.write(output)


def valid_function(func):
    """
    Determines if the function can be automatically generated.

    This function should be removed and unnecessary in the final product.

    :param func: function to validate
    :return: whether generation should proceed.
    """
    if func.classification is None:
        return False
    for i,arg in enumerate(func.args):
        ty = arg.data_type.proto_str()
        if ty is None:
            return False
    return True

if __name__ == '__main__':
    main(sys.argv)
