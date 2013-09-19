def backTrack(C, X, Y, i, j):
    if i == 0 or j == 0:
        return []
    elif X[i-1] == Y[j-1]:
        returnable = backTrack(C, X, Y, i-1, j-1)
        returnable.append(X[i-1])
        return returnable
    else:
        if C[i][j-1] > C[i-1][j]:
            return backTrack(C, X, Y, i, j-1)
        else:
            return backTrack(C, X, Y, i-1, j)
        
def LCS(X, Y):
    m = len(X)
    n = len(Y)
    # An (m+1) times (n+1) matrix
    C = [[0] * (n+1) for i in range(m+1)]
    for i in range(1, m+1):
        for j in range(1, n+1):
            if X[i-1] == Y[j-1]: 
                C[i][j] = C[i-1][j-1] + 1
            else:
                C[i][j] = max(C[i][j-1], C[i-1][j])
    return C

def getLCSLength(X, Y):
    C = LCS(X, Y)
    m = len(X)
    n = len(Y)
    return len(backTrack(C, X, Y, m, n))
    
if __name__=='__main__':
    X = [1, 2, 5, 3, 4, 6, 8, 7] 
    Y = [2, 6, 8, 3, 4, 6, 7]
    C = LCS(X, Y)
    m = len(X)
    n = len(Y)
    print "Some LCS: : ", backTrack(C, X, Y, m, n)
    print "Len LCS : ", getLCSLength(X, Y)