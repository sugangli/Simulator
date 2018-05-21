import numpy as np
import matplotlib.pyplot as plt
import os
import collections

class BarChart(object):
	def __init__(self, path_to_file):
		f = open(path_to_file)
		self.contents = f.readlines()
		f.close()

	def getTrafficMeanStd(self):
		sum_list = []
		for line in self.contents:
			strings = line.split()
			traffic = float(strings[1])
			sum_list.append(traffic)
		return sum(sum_list)/len(sum_list), np.std(sum_list)

	def getAveDelay(self):
		sum_list = []
		for line in self.contents:
			strings = line.split()
			delay = float(strings[2])
			sum_list.append(delay)
		return sum(sum_list)/len(sum_list), np.std(sum_list)
	def getTrafficMeanStdByTime(self):
		avelist = []
		stdlist = []
		sum_list = []
		prev = -1
		for line in self.contents:
			strings = line.split()
			time = float(strings[0])
			if prev == -1 or time != prev:
				prev = time
				if len(sum_list) != 0:
					avelist.append(sum(sum_list)/len(sum_list))
					stdlist.append(np.std(sum_list))
					sum_list = []
				
			traffic = float(strings[2])
			sum_list.append(traffic)
		avelist.append(sum(sum_list)/len(sum_list))
		stdlist.append(np.std(sum_list))
		return avelist, stdlist

	def getDelayMeanStdByTime(self):
		avelist = []
		stdlist = []
		sum_list = []
		prev = -1
		for line in self.contents:
			strings = line.split()
			time = float(strings[0])
			if prev == -1 or time != prev:
				prev = time
				if len(sum_list) != 0:
					avelist.append(sum(sum_list)/len(sum_list))
					stdlist.append(np.std(sum_list))
					sum_list = []
				
			traffic = float(strings[3])
			sum_list.append(traffic)
		avelist.append(sum(sum_list)/len(sum_list))
		stdlist.append(np.std(sum_list))
		return avelist, stdlist


if __name__ == '__main__':
	####### on path ########
	onpath_means_dic = {}
	onpath_stds_dic = {}
	onpath_file_path = "../summary_onpath/"
	onpath_files = os.listdir(onpath_file_path)
	for file in onpath_files:
		strings = file.split("_")
		num =  strings[-1].split(".")
		bc  = BarChart(onpath_file_path + file)
		mean, std = bc.getTrafficMeanStd()
		onpath_means_dic[int(num[0])] = mean
		onpath_stds_dic[int(num[0])] = std
	onpath_means = []
	onpath_stds = []
	od = collections.OrderedDict(sorted(onpath_means_dic.items()))
	for k, v in od.items():
		print("k:" + str(k) + " mean:" + str(v))
		onpath_means.append(v)
	od = collections.OrderedDict(sorted(onpath_stds_dic.items()))
	for k, v in od.items():
		print("k:" + str(k) + " std:" + str(v))
		onpath_stds.append(v)

	###### random #######
	random_means_dic = {}
	random_stds_dic = {}
	random_file_path = "../summary_random/"
	random_files = os.listdir(random_file_path)
	for file in random_files:
		strings = file.split("_")
		num =  strings[-1].split(".")
		bc  = BarChart(random_file_path + file)
		mean, std = bc.getTrafficMeanStd()
		random_means_dic[int(num[0])] = mean
		random_stds_dic[int(num[0])] = std
	random_means = []
	random_stds = []
	od = collections.OrderedDict(sorted(random_means_dic.items()))
	for k, v in od.items():
		print("k:" + str(k) + " mean:" + str(v))
		random_means.append(v)
	od = collections.OrderedDict(sorted(random_stds_dic.items()))
	for k, v in od.items():
		print("k:" + str(k) + " std:" + str(v))
		random_stds.append(v)

	###### kmed #####
	kmed_means_dic = {}
	kmed_stds_dic = {}
	kmed_file_path = "../summary_kmed/"
	kmed_files = os.listdir(kmed_file_path)
	for file in kmed_files:
		strings = file.split("_")
		num =  strings[-1].split(".")
		bc  = BarChart(kmed_file_path + file)
		mean, std = bc.getTrafficMeanStd()
		kmed_means_dic[int(num[0])] = mean
		kmed_stds_dic[int(num[0])] = std
	kmed_means = []
	kmed_stds = []
	od = collections.OrderedDict(sorted(kmed_means_dic.items()))
	for k, v in od.items():
		print("k:" + str(k) + " mean:" + str(v))
		kmed_means.append(v)
	od = collections.OrderedDict(sorted(kmed_stds_dic.items()))
	for k, v in od.items():
		print("k:" + str(k) + " std:" + str(v))
		kmed_stds.append(v)

	

	####### plot ########
	fig, ax = plt.subplots()
	n_groups = 5
	index = np.arange(n_groups)
	bar_width = 0.3

	opacity = 0.4
	error_config = {'ecolor': '0.3'}

	rects0 = plt.bar(index, onpath_means, bar_width,
	                 alpha=opacity,
	                 color='y',
	                 yerr=onpath_stds,
	                 error_kw=error_config,
	                 label='On-path')

	rects1 = plt.bar(index + bar_width, random_means, bar_width,
	                 alpha=opacity,
	                 color='b',
	                 yerr=random_stds,
	                 error_kw=error_config,
	                 label='FirstAssign')

	rects2 = plt.bar(index + bar_width * 2, kmed_means, bar_width,
	                 alpha=opacity,
	                 color='r',
	                 yerr=kmed_stds,
	                 error_kw=error_config,
	                 label='KMed')
	font_size = 20
	tick_size = 15
	plt.xlabel('# of Aggregators', fontsize = font_size)
	plt.ylabel('Traffic (Pkt #)', fontsize = font_size)
	plt.xticks(index + bar_width, ('4', '8', '16', '32', '64'), fontsize = tick_size)
	plt.yticks(fontsize = tick_size)
	plt.ticklabel_format(style='sci', axis='y', scilimits=(0,0))
	leg = plt.legend(fontsize = 14)
	if leg:
	    leg.draggable()
	plt.grid(axis = 'y')
	plt.tight_layout()
	plt.show()






