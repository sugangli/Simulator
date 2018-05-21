from BarChart import BarChart
import numpy as np
import matplotlib.pyplot as plt
import os
import collections

if __name__ == '__main__':
	###### onpath #######
	onpath_file_path = "../varied_timeout_summary/result_summary_64_Onpath.txt"

	bc = BarChart(onpath_file_path)
	onpath_ave_list, onpath_std_list = bc.getDelayMeanStdByTime()
	print(onpath_ave_list)
	print(onpath_std_list)

	###### random #######
	random_file_path = "../varied_timeout_summary/result_summary_64_Random.txt"

	bc = BarChart(random_file_path)
	random_ave_list, random_std_list = bc.getDelayMeanStdByTime()

	###### kmed #######
	kmed_file_path = "../varied_timeout_summary/result_summary_64_kmed.txt"

	bc = BarChart(kmed_file_path)
	kmed_ave_list, kmed_std_list = bc.getDelayMeanStdByTime()



	fig, ax = plt.subplots()
	index = np.arange(len(kmed_ave_list))
	bar_width = 0.3

	opacity = 0.4
	error_config = {'ecolor': '0.3'}

	rects0 = plt.bar(index, onpath_ave_list, bar_width,
	                 alpha=opacity,
	                 color='y',
	                 yerr=onpath_std_list,
	                 error_kw=error_config,
	                 label='On-path')

	rects1 = plt.bar(index + bar_width, random_ave_list, bar_width,
	                 alpha=opacity,
	                 color='b',
	                 yerr=random_std_list,
	                 error_kw=error_config,
	                 label='FirstAssign')

	rects2 = plt.bar(index + bar_width * 2, kmed_ave_list, bar_width,
	                 alpha=opacity,
	                 color='r',
	                 yerr=kmed_std_list,
	                 error_kw=error_config,
	                 label='KMed')
	font_size = 20
	tick_size = 15
	plt.xlabel('Time Window (s)', fontsize = font_size)
	plt.ylabel('Latency (s)', fontsize = font_size)
	plt.xticks(index + bar_width, (0.1, 0.2, 0.3, 0.4, 0.5, 0.6,0.7,0.8,0.9,1,1.1,1.2,1.3,1.4,1.5), rotation=45, fontsize = tick_size)
	plt.yticks(fontsize = tick_size)
	plt.ticklabel_format(style='sci', axis='y', scilimits=(0,0))
	leg = plt.legend(fontsize = tick_size)
	if leg:
	    leg.draggable()
	plt.grid(axis = 'y')
	plt.tight_layout()
	plt.show()
	
