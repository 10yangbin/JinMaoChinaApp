

// 赫夫曼树和赫夫曼编码的存储结构
function HuffmanNode(weight, parent, leftChild, rightChild) {
    this.weight = weight || 0;
    this.parent = parent || 0;
    this.leftChild = leftChild || 0;
    this.rightChild = rightChild || 0;
}



function huffManCoding(weights) {
    var n = weights.length;
    if (n < 1) return;

    var huffmanTree = buildHuffmanTree(weights, n);

    // 从叶子到根逆向求每个字符的赫夫曼编码
    var hc = calcHuffmanCode(huffmanTree, n);

    return [huffmanTree, hc];
}

function calcHuffmanCode(huffmanTree, n) {
    // 从叶子到根逆向求每个字符的赫夫曼编码
    var hc = [];
    var cd = [];
    for (var i = 0; i < n; i++) {
        var start = n - 1;
        for (var c = i, f = huffmanTree[i].parent; f != 0; c = f, f = huffmanTree[f].parent) {
            if (huffmanTree[f].leftChild == c) cd[--start] = '0';
            else cd[--start] = '1';
        }

        hc[i] = strCopy(cd, start);
    }

    return hc;
}

// 创建一棵叶子结点数为n的Huffman树
function buildHuffmanTree(weights, n) {
    n = n || weights.length;
    var m = 2 * n - 1;
    var huffmanTree = [];

    // 初始化
    for (var i = 0; i < n; i++)
        huffmanTree[i] = new HuffmanNode(weights[i], 0, 0, 0);
    for (; i < m; i++)
        huffmanTree[i] = new HuffmanNode(0, 0, 0, 0);

    for (i = n; i < m; i++) {
        // 在HT[1..i-1]选择parent为0且weight最小的两个结点，返回其序号为[s1, s2]
        var ret = select(huffmanTree, i);
        var s1 = ret[0];
        var s2 = ret[1];
        huffmanTree[s1].parent = i;
        huffmanTree[s2].parent = i;
        huffmanTree[i].leftChild = s1;
        huffmanTree[i].rightChild = s2;
        huffmanTree[i].weight = huffmanTree[s1].weight + huffmanTree[s2].weight;
    }

    return huffmanTree;
}

function strCopy(str, start) {
    var s = '';
    for (; str[start]; start++) {
        s += str[start];
    }
    return s;
}

function select(huffmanTree, len) {
    var ret = [];
    for (var i = 0; i < len; i++) {
        var node = huffmanTree[i];
        if (node.parent !== 0) continue;

        if (ret.length < 2) {
            ret.push(i);
        } else {
            var index = huffmanTree[ret[0]].weight > huffmanTree[ret[1]].weight
                ? 0 : 1;

            if (node.weight < huffmanTree[ret[index]].weight)
                ret[index] = i;
        }
    }

    if (ret[0] > ret[1]) {
        var temp = ret[0];
        ret[0] = ret[1];
        ret[1] = temp;
    }

    return ret;
}

console.log('-------huffman coding :------');
console.log(huffManCoding([5, 29, 7, 8, 14, 23, 3, 11]));