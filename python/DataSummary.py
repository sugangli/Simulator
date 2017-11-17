class DataSummary(object):
	def __init__(self, filename):
		f = open(filename)
		self.content = f.readlines()

	def getTotelTraffic(self):
		total_pkt = len(self.content)
		return total_pkt

	def getAveDelay(self):
		sumlist = []
		for line in self.content:
			stringlist = line.split()
			if "s1" in stringlist[1]:
				nowstrings = stringlist[0].split("=")
				now = float(nowstrings[1])
				laststring = stringlist[-1].replace("}", "").split(",")
				sent_time = float(laststring[-1])
				sumlist.append(now - sent_time)

		return sum(sumlist)/len(sumlist)

if __name__ == '__main__':
	ds = DataSummary("../static_result_4_aggregator/resultTraceoutputTrace.data2")
	print("Total Traffic (Pkt):" + str(ds.getTotelTraffic()))
	print("Ave. Delay:" + str(ds.getAveDelay()))




