# yutak
aim for hot resource detect,based on netty 
## struct
1. client
regularly report data to worker
2. worker
If the corresponding resources exceed the limit, handle different resources according to the rules,<br>
such as adding local keys, prohibiting reading, or expanding specific keys (zset, list), and then respond to the client
## next step
1. import config center
2. xx