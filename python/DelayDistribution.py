from DataSummary import DataSummary
import matplotlib.pyplot as plt
import numpy as np

if __name__ == '__main__':
	num = 64
	f = open("../summary_random/all_delay_" + str(num) +".txt", "a+")
	for x in range(0, 10):
		ds = DataSummary("../static_result_random_" + str(num) +"_aggregator/" + str(num) +"_resultTraceRandomoutputTrace.data" + str(x))
		delays = ds.getAlldDelay("s1")
		for item in delays:
			line = str(item) + "\n"
			f.write(line)
	f.close()
	