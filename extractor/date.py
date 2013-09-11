import sys, json, re

from lxml import etree 
from subprocess import Popen, PIPE, STDOUT

from lcs import *

parser = etree.HTMLParser()
token_delimiter = re.compile("\W") 

valid_tags = {
    "div" : None, 
    "a" :  None,
    "span" : None,
    "p" : None,
    
    "i" : None,
    "b" : None,
    "strong" : None,
    "u" : None,
    
    "h1" : None,
    "h2" : None,
    "h3" : None,
    "h4" : None,
    "h5" : None,
    "h6" : None,
    "h7" : None,
    
    "em" : None,
    "font" : None,
    "sub" : None,
    "sup" : None,
    "abbr" : None,
    "pre" : None,
    "blockquote" : None,
    "q" : None,
    "cite" : None,
    "label" : None,
    "td" : None,
    "li" : None,
    "th" : None
}
    

one_digit_regex = re.compile('.*\d.*')
def extract_date(txt):
    txt = txt.replace("\n", " ")
    p = Popen(['java', '-jar', 'nattyrunner/target/nattyrunner-1.0-SNAPSHOT-jar-with-dependencies.jar'], stdout=PIPE, stdin=PIPE)
    date = p.communicate(input=txt)[0]
    if date == "" or date == None:
        return None
    elif re.match(one_digit_regex, date) == None:
        return None
    else:
        print >> sys.stderr, "extract_date found a data : ", date, " :: for text : ", txt
        return date.strip()

def is_date(node):
    if node.text == None:
        return False
    date = extract_date(node.text)
    if date == None:
        return False
    else:
        print >> sys.stderr, "is_date found a data : ", date, " :: for node.text : ", node.text
        return True

def tokenize(s):
    tokens = token_delimiter.split(s.lower())
    out = []
    for t in tokens:
        if t != '':
            out.append(t)
    return out 

def get_title_node(root, valid_tags, bad_phrases):
    page_title = root.find(".//title").text.lower()
    for ph in bad_phrases:
        page_title = page_title.replace(ph.lower(), "")
    
    pTokens = tokenize(page_title)
    
    body = root.find(".//body")
    
    max_match_len = -1
    title_node = None
    for el in body.iter():
        if valid_tags.has_key(el.tag) and el.text:
            matchableTokens = tokenize(el.text)
            match_len = getLCSLength(pTokens, matchableTokens)
#             print ">>>> ", match_len, el
            if match_len > max_match_len:
                max_match_len = match_len
                title_node = el
                print ">>>> best :: ", max_match_len, title_node
                
    return title_node

def bfs_find(root, needle):
    visited = {}
    
    # TODO: pop is not optimal for list, takes O(n).. chnage it to something better
    queue = [root]
    
    while(len(queue) > 0):
        node = queue.pop(0)
        if visited.has_key(node):
            continue
        
        #ignore root
        if valid_tags.has_key(node.tag) and needle(node) and node != root:
            return node
        else:
            visited[node] = True
            par = node.getparent()
            if par != None:
                queue.append(par)
            for child in node.getchildren():
                queue.append(child)
            
    # not found
    return None
    
def main():
    
    bad_phrases = ["The Times of India"]
    
    root = etree.parse(open("test/a.html"), parser)
    
    title_node = get_title_node(root, valid_tags, bad_phrases)

    print "title_node : ", title_node, title_node.tag, title_node.text
    
    date_node = bfs_find(title_node, is_date)
    print "date_node : ---", date_node, "---"
    print "date_node : ", date_node, date_node.tag, date_node.text

    return root, title_node, date_node

if __name__=='__main__':
    main()
#xpath
# p = '//*[@id="netspidersosh"]/div[1]/div/div[10]/div[1]/span[1]/h1'
# tree.xpath(p)[0].textv