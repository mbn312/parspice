import yaml

templates = ['TypeSender.java', 'TypeArraySender.java', 'TypeMatrixSender.java']
template_dir = '../java/parspice/sender/'

outputs = ['Sender.java', 'ArraySender.java', 'MatrixSender.java']
output_dir = '../../main/java/parspice/sender/'

with open('../yaml/senders.yaml') as senders_file:
    # use safe_load instead load
    senders = yaml.safe_load(senders_file)

    for group in senders:
        for depth in range(3):
            with open(template_dir + templates[depth]) as template_file:
                template = template_file.read()
                output = template.replace("###NAME###", group['name']) \
                    .replace("###TYPE###", group['types'][depth]) \
                    .replace("###STREAM###", group['stream'])
                with open(output_dir + group['name'] + outputs[depth], 'w') as output_file:
                    output_file.write(output)