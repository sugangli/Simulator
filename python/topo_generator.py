class TopoGenerator(object):
	def __init__(self, rows, columns):
		self.rows = rows
		self.columns = columns

	def TopoGenerate(self, outputfile):
		print("Generating" + outputfile)
		file = open(outputfile, "a")
		bw1 = 1500
		bw2 = 1500
		latency = 0.01
		for i in range(0, self.rows):
			for j in range(0, self.columns):
				name = "c" + str(i * self.columns + j)
				if i < self.rows - 1 and j < self.columns - 1:
					line = name + "\t" + str(bw1) + "\t" + "c" + str((i + 1) * self.columns + j) + "\t" + str(bw2) + "\t" + str(latency) + "\n"
					file.write(line)
					line = name + "\t" + str(bw1) + "\t" + "c" + str(i * self.columns + j + 1) + "\t" + str(bw2) + "\t" + str(latency) + "\n"
					file.write(line)
				if i < self.rows - 1 and j == self.columns - 1:
					line = name + "\t" + str(bw1) + "\t" + "c" + str((i + 1) * self.columns + j) + "\t" + str(bw2) + "\t" + str(latency) + "\n"
					file.write(line)
				if i == self.rows - 1 and j < self.columns - 1:
					line = name + "\t" + str(bw1) + "\t" + "c" + str(i * self.columns + j + 1) + "\t" + str(bw2) + "\t" + str(latency) + "\n"
					file.write(line)

		file.close()
	def APGenerate(self, ap_per_node, outputfile):
		print("Generating" + outputfile)
		file = open(outputfile, "a")
		bw1 = 1500
		bw2 = 1500
		latency = 0.01
		for i in range(0, self.rows):
			for j in range(0, self.columns):
				for k in range(0, ap_per_node):
					ap_name = "ap" + str(k + (i * self.columns + j)* ap_per_node)
					c_name = "c" + str(i * self.columns + j)
					line = ap_name + "\t" + str(bw1) + "\t" + c_name + "\t" + str(bw2) + "\t" + str(latency) + "\n"
					file.write(line)



if __name__ == '__main__':
	r = 18
	c = 18
	ap_per_node = 6
	tg = TopoGenerator(r,c)
	tg.TopoGenerate("../data/topo_" + str(r) + "x" + str(c) +".txt")
	# tg.APGenerate(ap_per_node, "data/ap_topo_" + str(ap_per_node) + "_per_node_" + str(r) + "x" + str(c)+ ".txt")

