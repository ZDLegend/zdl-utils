## 对于json数据的解析
* JSONPath - 是xpath在json的应用。类似于XPath在xml文档中的定位，JsonPath表达式通常是用来路径检索或设置Json的。其表达式可以接受"dot–notation"和"bracket–notation"格式，例如$.store.book[0].title、$[‘store’][‘book’][0][‘title’]

### JSONPath 表达式
* JSONPath 用一个抽象的名字$来表示最外层对象。
* 使用.符号：$.store.book[0].title
* 使用[]:$['store']['book'][0]['title']
* 数组索引
1. JSONPath 允许使用通配符 * 表示所以的子元素名和数组索引。还允许使用 '..' 从E4X参照过来的和数组切分语法[start:end:step]
2. > $.store.book[(@.length-1)].title
3. 使用'@'符号表示当前的对象，?(<判断表达式>) 使用逻辑表达式来过滤 
>$.store.book[?(@.price < 10)].title

## JSONPath语法元素和对应XPath元素的对比
|XPath|JSONPath|Description|
|---|---|---|
|/|$|表示根元素|
|.|@|当前元素|
|/|. or []|子元素|
|..|n/a|父元素|
|//|..|递归下降，JSONPath是从E4X借鉴的。|
|*|*|通配符，表示所有的元素|
|@|n/a|属性访问字符|
|[]|[]|子元素操作符|
| |[,]|连接操作符在XPath 结果合并其它结点集合。JSONP允许name或者数组索引。|
|n/a|[start:end:step]|数组分割操作从ES4借鉴。|
|[]|?()|应用过滤表示式|
|n/a|()|脚本表达式，使用在脚本引擎下面。|
|()|n/a|Xpath分组|

## JSONPath使用举例
接口返回：
```
[
    {
        "id":"PRIMARY",
        "name":"小学",
        "front_id":"PRIMARY",
        "front_name":"小学"
    },
    {
        "id":"JUNIOR",
        "name":"初中",
        "front_id":"JUNIOR",
        "front_name":"初中"
    },
    {
        "id":"HIGH",
        "name":"高中",
        "front_id":"HIGH",
        "front_name":"高中"
    },
    {
        "id":"TECHNICAL",
        "name":"中专/技校",
        "front_id":"TECHNICAL",
        "front_name":"中专/技校"
    },
    {
        "id":"COLLEGE",
        "name":"大专",
        "front_id":"COLLEGE",
        "front_name":"大专"
    },
    {
        "id":"BACHELOR",
        "name":"本科",
        "front_id":"BACHELOR",
        "front_name":"本科"
    },
    {
        "id":"MASTER",
        "name":"硕士",
        "front_id":"MASTER",
        "front_name":"硕士"
    },
    {
        "id":"DOCTOR",
        "name":"博士",
        "front_id":"DOCTOR",
        "front_name":"博士"
    }
]
```

|JSONPath|结果|
|---|---|
|$.[*].name|               所有学历的name|
|$.[*].id|                 所有的id|
|$.[*]|                    所有元素|
|$.[(@.length-2)].name|    倒数第二个元素的name|
|$.[2]|                    第三个元素|
|$.[(@.length-1)]|         最后一个元素|
|$.[0,1] $.[:2]|           前面的两个元素|
|$.[?(@.name =~ /.*中/i)]| 过滤出所有的name包含"中"的书|
|$..book[?(@.price<10)]|   过滤出价格低于10的书|
|$.[*].length()|           所有元素的个数|

接口返回：
```
{
    "store": {
        "book": [
            {
                "category": "reference",
                "author": "Nigel Rees",
                "title": "Sayings of the Century",
                "price": 8.95
            },
            {
                "category": "fiction",
                "author": "Evelyn Waugh",
                "title": "Sword of Honour",
                "price": 12.99
            },
            {
                "category": "fiction",
                "author": "Herman Melville",
                "title": "Moby Dick",
                "isbn": "0-553-21311-3",
                "price": 8.99
            },
            {
                "category": "fiction",
                "author": "J. R. R. Tolkien",
                "title": "The Lord of the Rings",
                "isbn": "0-395-19395-8",
                "price": 22.99
            }
        ],
        "bicycle": {
            "color": "red",
            "price": 19.95
        }
    },
    "expensive": 10
}
```

### JsonPath表达式 结果
* $.store.book[*].author或 $..author
```
[
    "Nigel Rees",
    "Evelyn Waugh",
    "Herman Melville",
    "J. R. R. Tolkien"
]
```

* $.store.* 显示所有叶子节点值
```
[
    [
        {
            "category":"reference",
            "author":"Nigel Rees",
            "title":"Sayings of the Century",
            "price":8.95
        },
        {
            "category":"fiction",
            "author":"Evelyn Waugh",
            "title":"Sword of Honour",
            "price":12.99
        },
        {
            "category":"fiction",
            "author":"Herman Melville",
            "title":"Moby Dick",
            "isbn":"0-553-21311-3",
            "price":8.99
        },
        {
            "category":"fiction",
            "author":"J. R. R. Tolkien",
            "title":"The Lord of the Rings",
            "isbn":"0-395-19395-8",
            "price":22.99
        }
    ],
    {
        "color":"red",
        "price":19.95
    }
]
```
* $.store..price
```
[
    8.95,
    12.99,
    8.99,
    22.99,
    19.95
]
```

* $..book[0,1] 或 $..book[:2]
```
[
    {
        "category":"reference",
        "author":"Nigel Rees",
        "title":"Sayings of the Century",
        "price":8.95
    },
    {
        "category":"fiction",
        "author":"Evelyn Waugh",
        "title":"Sword of Honour",
        "price":12.99
    }
]
```
$..book[-2:] 获取最后两本书 

$..book[2:]
```
[
    {
        "category":"fiction",
        "author":"Herman Melville",
        "title":"Moby Dick",
        "isbn":"0-553-21311-3",
        "price":8.99
    },
    {
        "category":"fiction",
        "author":"J. R. R. Tolkien",
        "title":"The Lord of the Rings",
        "isbn":"0-395-19395-8",
        "price":22.99
    }
]
```

* $..book[?(@.isbn)] 所有具有isbn属性的书

* $.store.book[?(@.price < 10)] 所有价格小于10的书

* $..book[?(@.price <= $[‘expensive’])] 所有价格低于expensive字段的书

* $..book[?(@.author =~ /.*REES/i)] 所有符合正则表达式的书 
```
[
    {
        "category":"reference",
        "author":"Nigel Rees",
        "title":"Sayings of the Century",
        "price":8.95
    }
]
```
* $..* 返回所有

* $..book.length()
```
[
    4
]
```
## 过滤器
|操作符|描述|
|---|---|
|==|等于符号，但数字1不等于字符1(note that 1 is not equal to ‘1’)|
|!=|不等于符号|
|<|小于符号|
|<=|小于等于符号|
|>|大于符号|
|>=|大于等于符号|
|=~|判断是否符合正则表达式，例如[?(@.name =~ /foo.*?/i)]|
|in|所属符号，例如[?(@.size in [‘S’, ‘M’])]|
|nin|排除符号|
|size|size of left (array or string) should match right|
|empty|判空符号|

### 例如：
1. 所有具有isbn属性的书
> $.store.book[?(@.isbn)].author
2. 所有价格大于10的书
> $.store.book[?(@.price > 10)]
3. 查询xxx==3的所有对象
> $.result.list[?(@.xxx ==3)]
4. 可以自定义过滤器来获取想要的任何元素，可以多条件查询

## 在线解析器
http://jsonpath.com/
https://jsonpath.curiousconcept.com/