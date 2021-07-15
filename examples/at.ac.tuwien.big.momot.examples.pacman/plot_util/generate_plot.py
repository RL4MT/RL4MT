import os
import sys
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns


if len(sys.argv) > 4:
    print('You have specified too many arguments')
    sys.exit()

if len(sys.argv) < 4:
    print('You need to specify 3 arguments: 2 files from the pacman output folder (.csv): First parameters must be "PG_<NR>.csv, second parameter must be "qlearn_<NR>.csv,'
          'then as third argument an integer setting the range of the x-axis (seconds)')
    sys.exit()

pg_path = sys.argv[1]
qlearn_path = sys.argv[2]
x_axis_seconds = int(sys.argv[3])

qlearn = pd.read_csv(qlearn_path, sep=";").iloc[::10,2:4]
qlearn['runtime in ms'] = (qlearn['runtime in ms'] - qlearn['runtime in ms'].iloc[0])/1000.0
qlearn.rename(columns={'runtime in ms':'runtime in s'}, inplace=True)
qlearn["name"] = "$Q_{Basic}$"
pg = pd.read_csv(pg_path, sep=";").iloc[::10,2:4]
pg['runtime in ms'] = (pg['runtime in ms'] - pg['runtime in ms'].iloc[0])/1000.0
pg.rename(columns={'runtime in ms':'runtime in s'}, inplace=True)
pg["name"] = "PG"

if x_axis_seconds == 0:
    max_time_x = max(max(pg["runtime in s"]),max(qlearn["runtime in s"]))
else:
    max_time_x = x_axis_seconds

col = sns.color_palette("Set2")
sns.set_style("darkgrid", {"axes.facecolor": ".9"})

with plt.style.context('Solarize_Light2'):
    plt.plot(qlearn["runtime in s"], qlearn["averageReward"], color=col[2], alpha=0.9, label="$Q_{Basic}$")
    plt.plot(pg["runtime in s"], pg["averageReward"], color=col[1], alpha=0.9, label="PG")

sns.despine()
plt.xlim([0, max_time_x])
plt.ylim([-60, 35])
plt.yticks(np.arange(-60, 35, step=10), fontsize="12")
plt.xticks(np.arange(0, max_time_x+10, step=60),  fontsize="12")

plt.ylabel("Average Reward", fontsize="12.5")
plt.xlabel("Time in seconds", fontsize="12.5")
plt.legend()
plt.legend(loc="best", fontsize="large")

plt.savefig("average_reward.png", bbox_inches='tight')
