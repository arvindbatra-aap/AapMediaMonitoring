import logging
import time

class Timer:
    
    def __init__(self):
        self.start = int(time.time() * 1000)
    
    def count_lap(self, name):
        cur_time = int(time.time() * 1000)
        lap_time = cur_time - self.start
        self.start = cur_time
        logging.debug("[timer] %s took : %d ms" % (name, lap_time))
