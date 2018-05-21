import numpy as np
import matplotlib.pyplot as plt


def generateCost(dnum_mu, dnum_sig, nprod_mu, nprod_sig, d_mu, d_sig, n_mu, n_sig):
	iter_num = 1000
	dnums = np.random.normal(dnum_mu, dnum_sig, iter_num)
	nprods = np.random.normal(nprod_mu, nprod_sig, iter_num)
	cost = []

	for x in range(0, iter_num):
		num_d = int(dnums[x])
		if num_d < 0:
			continue
		ds = np.random.normal(d_mu, d_sig, num_d)
		sums = 0
		for j in range(0, len(ds)):
			if ds[j] < 0:
				continue
			num_prod = int(nprods[x])
			if num_prod < 0:
				continue
			prod = 1
			ns = np.random.normal(n_mu, n_sig, num_prod)
			for i in range(0, len(ns)):
				if ns[i] < 0:
					continue
				prod = prod * ns[i]
			sums = sums + ds[j]/prod
		# print(sums)
		cost.append(sums)

	filtered_cost = []
	for item in cost:
		if item >= 0 and item < np.percentile(cost, 95):
			filtered_cost.append(item)
	return filtered_cost
	print(np.percentile(filtered_cost, 95))


num_bins = 1000
mu = 20
sig = 10
times = 1
cost1 = generateCost(dnum_mu = mu, dnum_sig = sig, nprod_mu = times * mu, nprod_sig = times * sig, d_mu = mu, d_sig = sig, n_mu = times * mu, n_sig = times * sig)
counts, bin_edges = np.histogram(cost1, bins=num_bins, normed=False)
counts = counts.astype(float)/len(counts)
# Now find the cdf
cdf = np.cumsum(counts)
max_cdf = max(cdf)

norm_cdf = [x/max_cdf for x in cdf]
plt.plot(bin_edges[0:-1], norm_cdf, label = "Mean:" + str(mu) + " Std:" + str(sig))

mu = 15
sig = 7.5
cost1 = generateCost(dnum_mu = mu, dnum_sig = sig, nprod_mu = times * mu, nprod_sig = times * sig, d_mu = mu, d_sig = sig, n_mu = times * mu, n_sig = times * sig)
counts, bin_edges = np.histogram(cost1, bins=num_bins, normed=False)
counts = counts.astype(float)/len(counts)
# Now find the cdf
cdf = np.cumsum(counts)
max_cdf = max(cdf)

norm_cdf = [x/max_cdf for x in cdf]
plt.plot(bin_edges[0:-1], norm_cdf, label = "Mean:" + str(mu) + " Std:" + str(sig))


mu = 10
sig = 5
cost1 = generateCost(dnum_mu = mu, dnum_sig = sig, nprod_mu = times * mu, nprod_sig = times * sig, d_mu = mu, d_sig = sig, n_mu = times * mu, n_sig = times * sig)
counts, bin_edges = np.histogram(cost1, bins=num_bins, normed=False)
counts = counts.astype(float)/len(counts)
# Now find the cdf
cdf = np.cumsum(counts)
max_cdf = max(cdf)

norm_cdf = [x/max_cdf for x in cdf]
plt.plot(bin_edges[0:-1], norm_cdf, label = "Mean:" + str(mu) + " Std:" + str(sig))

mu = 5
sig = 2.5
cost1 = generateCost(dnum_mu = mu, dnum_sig = sig, nprod_mu = times * mu, nprod_sig = times * sig, d_mu = mu, d_sig = sig, n_mu = times * mu, n_sig = times * sig)
counts, bin_edges = np.histogram(cost1, bins=num_bins, normed=False)
counts = counts.astype(float)/len(counts)
# Now find the cdf
cdf = np.cumsum(counts)
max_cdf = max(cdf)

norm_cdf = [x/max_cdf for x in cdf]
plt.plot(bin_edges[0:-1], norm_cdf, label = "Mean:" + str(mu) + " Std:" + str(sig))
# count, bins, ignored = plt.hist(filtered_cost, bins=num_bins, normed=False)


font_size = 20
tick_size = 15
plt.xlabel('Cost', fontsize = font_size)
plt.ylabel('CDF', fontsize = font_size)
plt.xticks(fontsize = tick_size)
plt.yticks(fontsize = tick_size)
leg = plt.legend(fontsize = tick_size)
if leg:
	    leg.draggable()

plt.xscale('log')
plt.grid()
plt.tight_layout()
plt.show()
  
