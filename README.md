# A few words
&emsp;&emsp;依稀记得在当时写个全连接的反向传播应该算是深度学习的"Hello World"吧。 

&emsp;&emsp;这个repo是有点奇葩的，Java实现的framework， WTF？且听我解释(狡辩

&emsp;&emsp;临近毕业，我纠结于搞研发还是搞算法，搞研发那时Java大火，学习成本相对于C++没那么高；搞算法吧，自己又感觉对系统还有计算机底层没那么熟悉，所以想继续深入。网上看了很多Java相关的课程和视频，都是写一些购物或者商城系统，看了几节课确实没兴趣。这时候（命运的安排）无意中想到了自己对反向传播理解得不够深刻，每次看到Linear都会心里咯噔一下，它怎么backward，怎么求导？所以桌子一拍，决定了！我要写一个计算图框架，即使面试研发时面试官灵魂拷问，“你这个和后端开发似乎不搭边啊？”我也认了。（当然我还是给自己留了退路的，用Java写，这样总不能说完全无关吧？）

&emsp;&emsp; 这里的实现是我即兴发挥的（纯粹是觉得靠自己想出方案很有意思罢了，所以代码里可能充斥着不优雅）。当时根本不知道stride的概念，怎么实现多维Tensor操作全靠自己脑补，只是凭借着“啊，内存是1D连续的，但是所有语言里都有array[][]...[]这样的定义方式，所以多维Tensor肯定也是一样的思路”实现了一下。

### 更加优雅的类PyTorch玩具框架请看CMU的DLSys课程的Lab
[CMU DLSys](https://dlsyscourse.org/assignments/)

[个人的Lab实现与拓展](https://github.com/JZ-cs/DLSys/tree/main)

### 已实现的功能：
1. 基本操作：加减乘除和矩阵乘法;
2. reduce操作：sum，max(min还没写^_^);
3. 功能性操作：transpose，squeeze，unsqueeze;
4. 函数操作实现比较简单，因此只实现了exp，log，sigmoid，这块是最简单的；
5. 损失函数目前只有MSE。
6. 一个简单的Module模块，目的在于后续复杂的实现比如全连接层，卷积层以及自定义的模型，类似PyTorch。

&emsp;&emsp;所有已经实现的操作都支持多维张量，也都支持广播操作。

### 代码介绍
1.  所有代码均位于/src/main/java/下。
2.  operation包：定义了作为所有操作结点父类的Node, 以及相应的具体子操作结点。
3.  operation下的MultiVector.java，实现多维张量的主要代码，包括多维张量的定义，具体的计算操作等均在里面实现，操作Node(如MatmulNode)里面的计算也是调用MultiVector.java里的实现。
4.  network包和dataDistribute包，实现了简易的数据分布式训练功能（只在曾经拥有的3台阿里云ECS上试过没啥问题，当然有问题也不奇怪）。


### 大佬轻喷，欢迎提出意见，不论是关于Java的，还是实现上的。
