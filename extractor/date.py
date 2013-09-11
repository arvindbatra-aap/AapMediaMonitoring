import sys, re, traceback
from subprocess import Popen, PIPE, STDOUT
from collections import deque

from BeautifulSoup import *

from lcs import *

token_delimiter = re.compile("\W") 

invalid_tags = {
    "script" : None,
    "noscript" : None
}

one_digit_regex = re.compile('.*\d.*')
month_regex = re.compile('.*[\s^](jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*')
def extract_date(txt):
    txt = txt.replace("\n", " ")
    
    if re.match(month_regex, txt.lower()) == None or re.match(one_digit_regex, txt) == None:
       return None 
    
#     debug
#     return txt.strip()
    
    p = Popen(['java', '-jar', 'nattyrunner/target/nattyrunner-1.0-SNAPSHOT-jar-with-dependencies.jar'], stdout=PIPE, stdin=PIPE)
    date = p.communicate(input=txt)[0]
    if date == "" or date == None:
        return None
    else:
        print >> sys.stderr, "extract_date found a data : ", date, " :: for text : ", txt
        return date.strip()

def is_date(txt):
    if txt == None:
        return False
    date = extract_date(txt)
    if date == None:
        return False
    else:
        print >> sys.stderr, "is_date found a data : ", date, " :: for node.text : ", txt
        return True

def tokenize(s):
    tokens = token_delimiter.split(s.lower())
    out = []
    for t in tokens:
        if t != '':
            out.append(t)
    return out 

def is_leaf(node):
    if getattr(node, "name", None) is None and not node.isspace():
        return True
    else:
        return False

def get_title_node(soup, bad_phrases):
    page_title = soup.title.text
    for ph in bad_phrases:
        page_title = page_title.replace(ph.lower(), "")
    
    pTokens = tokenize(page_title)
    
    body = soup.body
    
    max_match_len = -1
    title_node = None
    
    #dfs
    stack = deque([soup.body])
    while len(stack) > 0:
        node = stack.pop()
        if is_leaf(node):
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
                
            matchableTokens = tokenize(txt)
            match_len = getLCSLength(pTokens, matchableTokens) / float(1 + len(matchableTokens))
#             print ">>>> ", match_len, el
            if match_len > max_match_len:
                max_match_len = match_len
                title_node = node
                print ">>>> best :: ", max_match_len, title_node
        else:
            try:
                if invalid_tags.has_key(node.name):
#                     print "discarding ", node.name
                    continue 
                for ch in node.contents:
                    stack.append(ch)
            except:
#                 traceback.print_exc()
                continue
    return title_node


def dfs_find(root):
    visited = {}
    
    # TODO: pop is not optimal for list, takes O(n).. chnage it to something better
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
        
        if is_leaf(node) or str(type(node)) == "<class 'BeautifulSoup.NavigableString'>":
            if str(type(node)) == "<class 'BeautifulSoup.NavigableString'>":
                txt = node
            else:
                try:
                    txt = node.text
                except:
                    continue
            if is_date(txt):
                return node
        else:
            try:
                if invalid_tags.has_key(node.name):
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
    
def get_date(html_text, bad_title_phrase_list):
    soup = BeautifulSoup(html_text)
    title_node = get_title_node(soup, bad_title_phrase_list)
    print "[debug] title_node : ", title_node, ",  par : ", title_node.parent, ', pp : ', title_node.parent.parent, title_node.parent.name, title_node.parent.parent.name 
    date_node = dfs_find(title_node)
    print "[debug] date_node : ", date_node
    return extract_date(date_node)
    
def main():
    
    bad_phrases = ["The Times of India"]
    soup = BeautifulSoup(open("test/a.html").read())
    
    title_node = get_title_node(soup, bad_phrases)
    print "title_node : ", title_node, ",  par : ", title_node.parent, title_node.parent.name, title_node.parent.parent.name 
    
    date_node = dfs_find(title_node)
    print "date_node : ", date_node

if __name__=='__main__':
    main()
     
# valid_tags = {
#     "body" : None,
#     "div" : None, 
#     "a" :  None,
#     "span" : None,
#     "p" : None,
#     
#     "i" : None,
#     "b" : None,
#     "strong" : None,
#     "u" : None,
#     
#     "h1" : None,
#     "h2" : None,
#     "h3" : None,
#     "h4" : None,
#     "h5" : None,
#     "h6" : None,
#     "h7" : None,
#     
#     "em" : None,
#     "font" : None,
#     "sub" : None,
#     "sup" : None,
#     "abbr" : None,
#     "pre" : None,
#     "blockquote" : None,
#     "q" : None,
#     "cite" : None,
#     "label" : None,
#     "td" : None,
#     "li" : None,
#     "th" : None
# }