## HashMap的结构
数组的寻址快，但是数据的插入与删除速度不行。 链表的插入与删除速度快，但是寻址速度不行。 那有没有一种两者兼具的数据结构，答案肯定是有的，那就是hash表。 HashMap 就是根据 数组+链表的方式组成了hash表：

![输入图片说明](https://images.gitee.com/uploads/images/2018/0830/173130_ca792251_87650.png "1.png")

## 对于HashMap的一些疑问

![输入图片说明](https://images.gitee.com/uploads/images/2018/0830/173144_4d97d72f_87650.png "2.png")

#### 一、HashMap的resize过程是什么样的？

HashMap在put的时候会先检查当前数组的length,如果插入新的值的时候使得length > 0.75f * size（f 为加载因子，可以在创建hashMap时指定）的话，会将数组进行扩容为当前容量的2倍。 扩容之后必定要将原有hashMap 中的值拷贝到新容量的hashMap 里面，HashMap 默认的容量为16，加载因子为0.75， 也就是说当HashMap 中Entry的个数超过 16 * 0.75 = 12时, 会将容量扩充为 16 * 2 = 32，然后重新计算元素在数组中的位置，这是一个非常耗时的操作，所以我们在使用HashMap的时候如果能预先知道Map中元素的大小，预设其大小能够提升其性能。 resize代码：


```
//HashMap数组扩容

void resize(int newCapacity) {

Entry[] oldTable = table;

int oldCapacity = oldTable.length;

//如果当前的数组长度已经达到最大值，则不在进行调整

if (oldCapacity == MAXIMUM_CAPACITY) {

threshold = Integer.MAX_VALUE;

return;

}

//根据传入参数的长度定义新的数组

Entry[] newTable = new Entry[newCapacity];

//按照新的规则，将旧数组中的元素转移到新数组中

transfer(newTable);

table = newTable;

//更新临界值

threshold = (int)(newCapacity * loadFactor);

}

//旧数组中元素往新数组中迁移

void transfer(Entry[] newTable) {

//旧数组

Entry[] src = table;

//新数组长度

int newCapacity = newTable.length;

//遍历旧数组

for (int j = 0; j < src.length; j++) {

Entry e = src[j];

if (e != null) {

src[j] = null;

do {

Entry next = e.next;

int i = indexFor(e.hash, newCapacity);//放在新数组中的index位置

e.next = newTable[i];//实现链表结构，新加入的放在链头，之前的的数据放在链尾

newTable[i] = e;

e = next;

} while (e != null);

}

}

}

```


这是1.7中的代码，1.8中引入了红黑树的概念，代码会相对复杂一些。

#### 二、HashMap在扩容的时候为什么容量都是原来的2倍，即容量为2^n

HashMap 在计算数组中key的位置时，使用的算法为：
/* * Returns index for hash code h. */
static int indexFor(int h, int length) {
// assert Integer.bitCount(length) == 1 : “length must be a non-zero power of 2”; return h & (length-1); }

即对key的hashcode 与当前数组容量 -1 进行与操作 我们假设有一个容量为分别为15 和 16 的hashMap ，有两个key的hashcode 分别为4和5，进行indexFor操作之后：

H & (length -1) hash & table.length-1 4 & (15 - 1) 0100 & 1110 = 0100 5 & （ 15 -1 ） 0101 & 1110 = 0100
4 & (16 - 1) 0100 & 1111 = 0100 5 & （ 16 -1 ） 0101 & 1111 = 0101

我们能够看到在容量为16时进行indexFor操作之后获得相同结果的几率要比容量为15时的几率要小，这样能够减少出现hash冲突的几率，从而提高查询效率。2 ^ n是一个非常神奇的数字。

#### 三、put时出现相同的hashcode会怎样？

hashMap 里面存储的Entry对象是由数组和链表组成的，当key的hashcode相同时，数组上这个位置存储的结构就是链表，这时会将新的值插入链表的表头。进行取值的时候会先获取到链表，再对链表进行遍历，通过key.equals方法获取到值。（hashcode相同不代表对象相同，不要混淆hashcode和equals方法） 所以声明作final的对象，并且采用合适的equals()和hashCode()方法的话，将会减少碰撞的发生，提高效率。不可变性使得能够缓存不同键的hashcode，这将提高整个获取对象的速度，使用String，Interger这样的wrapper类作为键是非常好的选择。

#### 四、什么是循环链表？

HashMap在遇到多线程的操作中，如果需要重新调整HashMap的大小时，多个线程会同时尝试去调整HashMap的大小，这时处在同一位置的链表的元素的位置会反过来，以为移动到新的bucket的时候，HashMap不会将新的元素放到尾部（为了避免尾部遍历），这时可能会出现A -> B -> A的情况，从而出现死循环，这便是HashMap中的循环链表。 所以HashMap 是不适合用在多线程的情况下的，可以考虑尝试使用HashTable 或是 ConcurrentHashMap

#### 五、如何正确使用HashMap提高性能

在设置HashMap的时候指定其容量的大小，减少其resize的过程。

#### 六、JDK1.8对HashMap进行了哪些优化

jdk1.8在对hash冲突的key时，如果此bucket位置上的元素数量在8以下时，还是和原来一样使用链表来进行存储，这时寻址的时间复杂度为O(n),当元素数量超过8时，使用红黑树进行代替，这时寻址的时间复杂度为O(n)

我们看看官方文档中的一段描述:

```
Because TreeNodes are about twice the size of regular nodes, we
use them only when bins contain enough nodes to warrant use
(see TREEIFY_THRESHOLD). And when they become too small (due to
removal or resizing) they are converted back to plain bins. In
usages with well-distributed user hashCodes, tree bins are
rarely used. Ideally, under random hashCodes, the frequency of
nodes in bins follows a Poisson distribution
(http://en.wikipedia.org/wiki/Poisson_distribution) with a
parameter of about 0.5 on average for the default resizing
threshold of 0.75, although with a large variance because of
resizing granularity. Ignoring variance, the expected
occurrences of list size k are (exp(-0.5) * pow(0.5, k) /
factorial(k)). The first values are:

0: 0.60653066
1: 0.30326533
2: 0.07581633
3: 0.01263606
4: 0.00157952
5: 0.00015795
6: 0.00001316
7: 0.00000094
8: 0.00000006
more: less than 1 in ten million

```


#### 七、HashMap 与 HashTable、ConcurrentHashMap的区别

1.HashTable的方法是同步的，在方法的前面都有synchronized来同步，HashMap未经同步，所以在多线程场合要手动同步

2.HashTable不允许null值(key和value都不可以) ,HashMap允许null值(key和value都可以)。

3.HashTable有一个contains(Object value)功能和containsValue(Object value)功能一样。

4.HashTable使用Enumeration进行遍历，HashMap使用Iterator进行遍历。

5.HashTable中hash数组默认大小是11，增加的方式是 old*2+1。HashMap中hash数组的默认大小是16，而且一定是2的指数。

6.哈希值的使用不同，HashTable直接使用对象的hashCode，而HashMap重新计算hash值，用与代替求

7.ConcurrentHashMap也是一种线程安全的集合类，他和HashTable也是有区别的，主要区别就是加锁的粒度以及如何加锁，ConcurrentHashMap的加锁粒度要比HashTable更细一点。将数据分成一段一段的存储，然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据的时候，其他段的数据也能被其他线程访问。


#### 八、ConcurrentHashMap 和 Hashtable 的区别

ConcurrentHashMap 和 Hashtable 的区别主要体现在实现线程安全的方式上不同。

##### 底层数据结构：

JDK1.7的 ConcurrentHashMap 底层采用 分段的数组+链表 实现，JDK1.8 采用的数据结构跟HashMap1.8的结构一样，数组+链表/红黑二叉树。Hashtable 和 JDK1.8 之前的 HashMap 的底层数据结构类似都是采用 数组+链表 的形式，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突而存在的；

##### 实现线程安全的方式（重要）：

在JDK1.7的时候，ConcurrentHashMap（分段锁） 对整个桶数组进行了分割分段(Segment)，每一把锁只锁容器其中一部分数据，多线程访问容器里不同数据段的数据，就不会存在锁竞争，提高并发访问率。（默认分配16个Segment，比Hashtable效率提高16倍。） 到了 JDK1.8 的时候已经摒弃了Segment的概念，而是直接用 Node 数组+链表+红黑树的数据结构来实现，并发控制使用 synchronized 和 CAS 来操作。（JDK1.6以后 对 synchronized锁做了很多优化） 整个看起来就像是优化过且线程安全的 HashMap，虽然在JDK1.8中还能看到 Segment 的数据结构，但是已经简化了属性，只是为了兼容旧版本；

Hashtable(同一把锁) :使用 synchronized 来保证线程安全，效率非常低下。当一个线程访问同步方法时，其他线程也访问同步方法，可能会进入阻塞或轮询状态，如使用 put 添加元素，另一个线程不能使用 put 添加元素，也不能使用 get，竞争会越来越激烈效率越低。

#### 九、HashMap 多线程操作导致死循环问题

在多线程下，进行 put 操作会导致 HashMap 死循环，原因在于 HashMap 的扩容 resize()方法。由于扩容是新建一个数组，复制原数据到数组。由于数组下标挂有链表，所以需要复制链表，但是多线程操作有可能导致环形链表。复制链表过程如下:
以下模拟2个线程同时扩容。假设，当前 HashMap 的空间为2（临界值为1），hashcode 分别为 0 和 1，在散列地址 0 处有元素 A 和 B，这时候要添加元素 C，C 经过 hash 运算，得到散列地址为 1，这时候由于超过了临界值，空间不够，需要调用 resize 方法进行扩容，那么在多线程条件下，会出现条件竞争，模拟过程如下：

线程一：读取到当前的 HashMap 情况，在准备扩容时，线程二介入

![输入图片说明](https://images.gitee.com/uploads/images/2018/0902/124903_58cfe293_87650.jpeg "11.jpg")

线程二：读取 HashMap，进行扩容

![输入图片说明](https://images.gitee.com/uploads/images/2018/0902/124909_0bb789e7_87650.jpeg "22.jpg")

线程一：继续执行

![输入图片说明](https://images.gitee.com/uploads/images/2018/0902/124915_5a89ac2e_87650.jpeg "33.jpg")

这个过程为，先将 A 复制到新的 hash 表中，然后接着复制 B 到链头（A 的前边：B.next=A），本来 B.next=null，到此也就结束了（跟线程二一样的过程），但是，由于线程二扩容的原因，将 B.next=A，所以，这里继续复制A，让 A.next=B，由此，环形链表出现：B.next=A; A.next=B

### 推荐阅读：

- [jdk1.8中ConcurrentHashMap的实现原理](https://blog.csdn.net/fjse51/article/details/55260493)
- [HashMap? ConcurrentHashMap? 相信看完这篇没人能难住你！](https://crossoverjie.top/2018/07/23/java-senior/ConcurrentHashMap/) 
- [HASHMAP、HASHTABLE、CONCURRENTHASHMAP的原理与区别](http://www.yuanrengu.com/index.php/2017-01-17.html)
- [ConcurrentHashMap实现原理及源码分析](https://www.cnblogs.com/chengxiao/p/6842045.html)
- [java-并发-ConcurrentHashMap高并发机制-jdk1.8](https://blog.csdn.net/jianghuxiaojin/article/details/52006118#commentBox)

- [面试中并发类问题的准备和学习](https://www.imooc.com/article/23969)

