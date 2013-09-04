#!/bin/bash

wget -q -O - http://delhi.aamaadmiparty.org/Delhi-Elections-2013/CandidateList \
  | grep 'DeclaredArvind Kejriwal' \
  | head -1 \
  | sed 's/[0-9]/\n/g' \
  | grep 'Candidate Declared' \
  | sed 's/Candidate Declared/\t/'
