import logging
import sys, re, traceback
from subprocess import Popen, PIPE, STDOUT
from collections import deque

from BeautifulSoup import *
from lcs import *
from timer import Timer
from natty import *

# Set logging level
#logging.basicConfig(level=logging.DEBUG)

class DateExtractor():
    
    token_delimiter = re.compile("\W")
    invalid_tags = {
        "script" : None,
        "noscript" : None
    }    
    one_digit_regex = re.compile('.*\d.*')
    month_regex = re.compile('.*[\s^](jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*')

    def __init__(self):
        self.natty = Natty()

    def extract_date(self, txt):
        txt = txt.replace("\n", " ")
        
        """ hard-coded rules to counter false positives from natty"""
        if re.match(self.month_regex, txt.lower()) == None or re.match(self.one_digit_regex, txt) == None:
           return None
        
        """making a call to natty"""
        return self.natty.extract_date(txt)

    def is_date(self, txt):
        if txt == None:
            return False
        date = self.extract_date(txt)
        if date == None:
            return False
        else:
            logging.debug("is_date found a data : %s :: for node.text : %s" % (date, txt))
            return True
    
    def tokenize(self, s):
        tokens = self.token_delimiter.split(s.lower())
        out = []
        for t in tokens:
            if t != '':
                out.append(t)
        return out 
    
    def is_leaf(self, node):
        if getattr(node, "name", None) is None and not node.isspace():
            return True
        else:
            return False
    
    def get_title_node(self, soup, bad_phrases):
        page_title = soup.title.text
        for ph in bad_phrases:
            page_title = page_title.replace(ph.lower(), "")
        
        pTokens = self.tokenize(page_title)
        
        body = soup.body
        
        max_match_len = -1
        title_node = None
        
        """dfs into whole DOM to get leaf node with maximum Jaccard coefficient with page title"""
        stack = deque([soup.body])
        while len(stack) > 0:
            node = stack.pop()
            if self.is_leaf(node):
                if str(type(node)) == "<class 'BeautifulSoup.NavigableString'>":
                    txt = node
                    tag = node.parent.name
                    ptag = node.parent.parent.name
                else:
                    try:
                        txt = node.text
                    except:
                        continue
                    tag = node.pname
                    ptag = node.parent.name
                # discard anchors
                if tag == 'a' or ptag == 'a':
                    continue
                    
                matchableTokens = self.tokenize(txt)
                match_len = getLCSLength(pTokens, matchableTokens) / float(1 + len(matchableTokens))
    #             print ">>>> ", match_len, el
                if match_len > max_match_len:
                    max_match_len = match_len
                    title_node = node
                    logging.debug(">>>> best :: %s, %s" % (str(max_match_len), str(title_node)))
            else:
                try:
                    if self.invalid_tags.has_key(node.name):
    #                     print "discarding ", node.name
                        continue 
                    for ch in node.contents:
                        stack.append(ch)
                except:
    #                 traceback.print_exc()
                    continue
        return title_node
    
    
    def dfs_find(self, root):
        visited = {}
        stack = deque([root])
        while(len(stack) > 0):
            node = stack.pop()
            if visited.has_key(node):
                continue
            else:
                visited[node] = True
            
            try:
                if node.parent.name != "body":
                    stack.append(node.parent)
            except:
                traceback.print_exc()
                pass
            
            if self.is_leaf(node) or str(type(node)) == "<class 'BeautifulSoup.NavigableString'>":
                if str(type(node)) == "<class 'BeautifulSoup.NavigableString'>":
                    txt = node
                else:
                    try:
                        txt = node.text
                    except:
                        continue
                if self.is_date(txt):
                    return node
            else:
                try:
                    if self.invalid_tags.has_key(node.name):
    #                     print "discarding ", node.name
                        continue
                    ch_list = node.contents
                    ch_list.reverse() 
                    for ch in ch_list:
                        stack.append(ch)
                except:
                    traceback.print_exc()
                    continue
            
        # not found
        return None
    
    def get_date(self, html_text, bad_title_phrase_list):
        t = Timer()
        t_all = Timer()
        
        soup = BeautifulSoup(str(html_text))
        t.count_lap("Parsing into a soup")
       
        title_node = self.get_title_node(soup, bad_title_phrase_list)
        logging.debug(" title_node : %s, par : %s" % (str(title_node), str(title_node.parent)))     #, ', pp : ', title_node.parent.parent, title_node.parent.name, title_node.parent.parent.name
        t.count_lap("Getting the title node")
        
        date_node = self.dfs_find(title_node)
        logging.debug(" date_node : %s" % (date_node))
        t.count_lap("Getting the date node")
        
        extracted_date = self.extract_date(date_node)
        t.count_lap("Getting the exact date")
        t_all.count_lap("Everything")
        
        return extracted_date
    
    def extract(self, datum):
        date_str = self.get_date(datum["raw_html"], [])
        return date_str

"""test cases"""
def test_raw():
    de = DateExtractor()
    
    bad_phrases = ["The Times of India"]
    soup = BeautifulSoup(open("test/a.html").read())
    
    title_node = de.get_title_node(soup, bad_phrases)
    print "title_node : ", title_node, ",  par : ", title_node.parent, title_node.parent.name, title_node.parent.parent.name 
    
    date_node = de.dfs_find(title_node)
    print "date_node : ", date_node

def test():
    de = DateExtractor()
    print de.extract({"source" : open("test/a.html").read()})

if __name__=='__main__':
    test()
