import numpy as np
import matplotlib.pyplot as plt
# Choose how many bins you want here
num_bins = 200

onpath_delays = np.loadtxt("../delay_summary/onpath_all_delay_64.txt")

# Use the histogram function to bin the data
onpath_counts, onpath_bin_edges = np.histogram(onpath_delays, bins=num_bins, normed=True)
onpath_counts = onpath_counts.astype(float)/len(onpath_delays)
# Now find the cdf
onpath_cdf = np.cumsum(onpath_counts)
max_onpath_cdf = max(onpath_cdf)

norm_onpath_cdf = [x/max_onpath_cdf for x in onpath_cdf]
# And finally plot the cdf
plt.plot(onpath_bin_edges[0:-1], norm_onpath_cdf, label = "Onpath")


random_delays = np.loadtxt("../delay_summary/random_all_delay_64.txt")

# Use the histogram function to bin the data
random_counts, random_bin_edges = np.histogram(random_delays, bins=num_bins, normed=True)
random_counts = random_counts.astype(float)/len(random_delays)

# Now find the cdf
random_cdf = np.cumsum(random_counts)
max_random_cdf = max(random_cdf)

norm_random_cdf = [x/max_random_cdf for x in random_cdf]
# And finally plot the cdf
plt.plot(random_bin_edges[0:-1], norm_random_cdf, label = "FirstAssign")

kmed_delays = np.loadtxt("../delay_summary/kmed_all_delay_64.txt")

# Use the histogram function to bin the data
kmed_counts, kmed_bin_edges = np.histogram(kmed_delays, bins=num_bins, normed=True)


# Now find the cdf
kmed_cdf = np.cumsum(kmed_counts)
max_kmed_cdf = max(kmed_cdf)

norm_kmed_cdf = [x/max_kmed_cdf for x in kmed_cdf]

# And finally plot the cdf
plt.plot(kmed_bin_edges[0:-1], norm_kmed_cdf, label = "KMed")






font_size = 20
tick_size = 15
plt.xlabel('Latency (s)', fontsize = font_size)
plt.ylabel('CDF', fontsize = font_size)
plt.xticks(fontsize = tick_size)
plt.yticks(fontsize = tick_size)

plt.legend(fontsize = tick_size)
plt.grid()
plt.tight_layout()
plt.show()
 