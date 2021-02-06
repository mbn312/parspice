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
    out = os.path.join('build', 'generated', 'sources', 'automation', 'main', 'java')
    if not os.path.exists(out):
        os.makedirs(out)

    for func in functions:
        try:
            generate_java(func, ['Call.java', 'Batch.java'], out, template_dir)
        except ValueError:
            print('not yet working: %s' % func.name)


def generate_java(func, templates, out, template_dir):

    upper_name = func.name.capitalize()
    lower_name = func.name

    fields = ''
    args = ''
    args_no_types = ''
    assign_fields = ''

    for i, arg in enumerate(func.args):

        fields += 'public %s %s;\n' % (arg.data_type, arg.name)

        args += '%s %s, ' % (arg.data_type, arg.name)
        args_no_types += '%s, ' % arg.name
        assign_fields += 'this.%s = %s;\n' % (arg.name, arg.name)
    

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
                .replace('###ASSIGN_FIELDS###', assign_fields)
            with open('%s/%s%s' % (out, upper_name, template), 'w') as out_file:
                out_file.write(output)


if __name__ == '__main__':
    main(sys.argv)
