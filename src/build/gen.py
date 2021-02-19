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


    generate_proto(functions, proto_out, template_dir)
    generate_factory(functions, java_out, template_dir)

    for func in functions:
        if func.classification == parse_tree.Classification.NORMAL:
            try:
                generate_java(func, ['Call.java', 'Batch.java'], java_out, template_dir)
            except ValueError:
                print('not yet working: %s' % func.name)


def generate_factory(funcs, out, template_dir):
    factories = ''
    imports = ''
    for func in funcs:
        if func.classification == parse_tree.Classification.NORMAL:
            bad_arg = False
            for i,arg in enumerate(func.args):
                ty = java_type_to_proto(arg.data_type)
                if ty is None:
                    bad_arg = True
                    break
            if bad_arg:
                continue

            upper_name = func.name[0].upper() + func.name[1:]
            lower_name = func.name

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
            factories += """
            public void %s() {
                // throw new NotImplementedException("aaaa");
            }
            """ % (func.name)
    with open(os.path.join(template_dir, 'ParSpice.java'), 'r') as template:
        proto = template.read() \
            .replace('###FACTORIES###', factories) \
            .replace('###IMPORTS###', imports)
        with open(os.path.join(out, 'ParSpice.java'), 'w') as out_file:
            out_file.write(proto)



def generate_proto(funcs, out, template_dir):
    services = ''
    messages = ''
    for func in funcs:
        if func.classification == parse_tree.Classification.NORMAL:
            upper_name = func.name[0].upper() + func.name[1:]
            inputs = ''
            outputs = ''

            bad_arg = False

            output_arg_counter = 1
            for i,arg in enumerate(func.args):
                ty = java_type_to_proto(arg.data_type)
                if ty is None:
                    bad_arg = True
                    break
                if arg.io == parse_tree.IO.INPUT or arg.io == parse_tree.IO.BOTH:
                    inputs += '%s %s = %i;\n' % (ty, arg.name, i+1)
                if arg.io == parse_tree.IO.OUTPUT or arg.io == parse_tree.IO.BOTH:
                    outputs += '%s %s = %i;\n' % (ty, arg.name, i+1)
            if bad_arg:
                continue

            if func.return_type.base_type != parse_tree.DataType.VOID:
                ty = java_type_to_proto(func.return_type)
                outputs += '%s ret = %i;\n' % (ty, len(func.args) + 1)


            services += 'rpc ###UPPER_NAME###RPC (###UPPER_NAME###Request) returns (###UPPER_NAME###Response);\n' \
                .replace('###UPPER_NAME###', upper_name)
            messages += """
            message ###UPPER_NAME###Request {
                message ###UPPER_NAME###Input {
                    ###INPUTS###
                }
                repeated ###UPPER_NAME###Input inputs = 1;
            }
            
            message ###UPPER_NAME###Response {
                message ###UPPER_NAME###Output {
                    ###OUTPUTS###
                }
                repeated ###UPPER_NAME###Output outputs = 1;
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

def java_type_to_proto(java):
    if java.array_depth in [0,1]:
        ty = ''
        if java.base_type == parse_tree.DataType.VOID:
            return None
        elif java.base_type == parse_tree.DataType.INT:
            ty = 'int32'
        elif java.base_type == parse_tree.DataType.BOOLEAN:
            ty = 'bool'
        elif java.base_type == parse_tree.DataType.GFSEARCHUTILS:
            return None
        elif java.base_type == parse_tree.DataType.GFSCALARQUANTITY:
            return None
        else:
            ty = java.base_to_str().lower()
        if java.array_depth == 1:
            ty = "repeated " + ty
        return ty
    elif java.array_depth == 2:
        if java.base_type == parse_tree.DataType.DOUBLE:
            return 'repeated RepeatedDouble'
        elif java.base_type == parse_tree.DataType.INT:
            return 'repeated RepeatedInt'
        else:
            return None



def generate_java(func, templates, out, template_dir):

    upper_name = func.name[0].upper() + func.name[1:]
    lower_name = func.name

    fields = ''
    args = ''
    args_no_types = ''
    assign_fields = ''
    builders = ''
    nested_builders = ''

    for i,arg in enumerate(func.args):
        if arg.data_type.base_type == parse_tree.DataType.GFSEARCHUTILS \
            or arg.data_type.base_type == parse_tree.DataType.GFSCALARQUANTITY:
            return

        cap_name = arg.name[0].upper() + arg.name[1:]

        object_type = str(arg.data_type).replace('int', 'Integer').replace('double', 'Double').replace('boolean', 'Boolean')
        base_object_type = arg.data_type.base_to_str().replace('int', 'Integer').replace('double', 'Double').replace('boolean', 'Boolean')

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
                base = arg.data_type.base_to_str().capitalize()
                nested_builders += """
                ArrayList<Repeated%s> nested%i = new ArrayList<Repeated%s>();
                for (%s[] row : call.%s) {
                    nested%i.add(
                        Repeated%s.newBuilder()
                            .addAllArray(Arrays.asList(row))
                            .build()
                    );
                }
                """ % (base, i, base, base_object_type, arg.name, i, base)
                builders += ".addAll%s(nested%i)\n" % (cap_name, i)
            else:
                return

    args = args[:-2]
    args_no_types = args_no_types[:-2]

    if func.return_type.base_type != parse_tree.DataType.VOID:
        fields += 'public %s ret;\n' % func.return_type

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
                .replace('###NESTED_BUILDERS###', nested_builders)
            with open(os.path.join(out, '%s%s' % (upper_name, template)), 'w') as out_file:
                out_file.write(output)


if __name__ == '__main__':
    main(sys.argv)
