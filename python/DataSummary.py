class DataSummary(object):
	def __init__(self, filename):
		f = open(filename)
		self.content = f.readlines()

	def getTotelTraffic(self):
		total_pkt = len(self.content)
		return total_pkt

	def getAveDelay(self, nodename):
		sumlist = []
		for line in self.content:
			stringlist = line.split()
			if nodename in stringlist[1]:
				nowstrings = stringlist[0].split("=")
				now = float(nowstrings[1])
				laststring = stringlist[-1].replace("}", "").split(",")
				sent_time = float(laststring[-1])
				sumlist.append(now - sent_time)
		if len(sumlist) != 0:
			return sum(sumlist)/len(sumlist)
		return -1
	def getAllDelay(self, nodename):
		sumlist = []
		for line in self.content:
			stringlist = line.split()
			if nodename in stringlist[1]:
				nowstrings = stringlist[0].split("=")
				now = float(nowstrings[1])
				laststring = stringlist[-1].replace("}", "").split(",")
				sent_time = float(laststring[-1])
				sumlist.append(now - sent_time)
		return sumlist

	def getMaxDelay(self):
		max_delay = -1
		for line in self.content:
			stringlist = line.split()
			nowstrings = stringlist[0].split("=")
			now = float(nowstrings[1])
			laststring = stringlist[-1].replace("}", "").split(",")
			sent_time = float(laststring[-1])
			if max_delay < now - sent_time:
				max_delay = now -sent_time		
		return max_delay

if __name__ == '__main__':
	num = "64"
	f = open("../summary_random/result_summary_" + num +".txt", "a+")
	for x in range(0, 10):
		ds = DataSummary("../static_result_random_" + num +"_aggregator/" + num +"_resultTraceRandomoutputTrace.data" + str(x))
		total = ds.getTotelTraffic()
		ave_delay = ds.getAveDelay("s1")
		max_d = ds.getMaxDelay()
		print(str(x) + " Total Traffic (Pkt):" + str(total))
		print("Ave. Delay:" + str(ave_delay))
		print("Max Delay:" + str(max_d))
		line  = str(x) + " " + str(total) + " " + str(ave_delay) + "\n"
		f.write(line)
	f.close()




