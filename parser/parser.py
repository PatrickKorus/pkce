import re
import numpy as np
import matplotlib.pyplot as plt

file = open('log3.txt', 'r')
log = file.read().replace('\n', ' ')
m = re.findall('\s([0-9]+\.?[0-9]*)', log)
a = np.array(m).astype(np.float)
columns = int(a.size/8)
a = a.reshape(columns, 8)
# Iterations to go - 0
# Verkehrsdichte (prozent) - 1
# Anteil Störer - 2
# SpeedFactor - 3
# Autos - 4
# Eingangsverkehrsdichte - 5
# Ausgangsverkehrsdichte - 6
# Durchschnittsgeschwindigkeit - 7
print(a)

traffic_densities = np.unique(a[:, 1])
values = np.array([])
groups = []
for density in traffic_densities:
    idx = (a[:, 1] == density)
    cvals = a[idx, :]
    idx = np.argsort(cvals[:, 2])
    values = np.append(values, cvals[:, 7])
    groups.append(cvals[idx, 7])
colors = np.repeat(['b','r','g','y'], 3)

groups = list(map(list, zip(*groups)))

plt.figure(figsize=(20,10))
xticks = [0.1, 1.1, 2.1, 3.1]
coop_labels = ['100% Kooperationsanteil', '85% Kooperationsanteil', '50% Kooperationsanteil', '15% Kooperationsanteil']
group_labels = traffic_densities
num_items = len(group_labels)
ind = np.arange(num_items)
width = 0.1
s = plt.subplot(1,1,1)
for num, vals in enumerate(groups):
    group_len = len(vals)
    print(vals)
    gene_rects = plt.bar(ind, vals, width=width, align="center", label=coop_labels[num])
    ind = ind + width
num_groups = len(group_labels)
# Make label centered with respect to group of bars
# Is there a less complicated way?
offset = (num_groups / 2.) * width
xticks = np.arange(num_groups) + offset
s.set_xticks(xticks)
plt.xlim([0 - width, max(xticks) + (num_groups * width)])
s.set_xticklabels(group_labels)
plt.legend()
plt.ylabel('Durchschnittsgeschwindigkeit [km/h]')
plt.xlabel('Verkehrsdichte')
plt.show()