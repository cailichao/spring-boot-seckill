## 优化

#### 负向查询不能使用索引

```sql
select * from order where status!=0 and stauts!=1
```

还有 not in/not exists都不是好习惯

```sql
select name from order where status not in (0,1);
```


可以优化为in查询：

```sql
select * from order where status in(2,3)
```


#### 前导模糊查询不能使用索引
如:

```sql
select name from user where name like '%xxx'
```

非前导则可以:
```sql
select name from user where name like 'xxx%'
```

MyISAM 存储引擎也可以做全文检索，不过只支持英文，相信现在应该也没人使用它了。建议使用` solr` 、` es` 等第三方开始工具实现全文检索功能。

#### 数据区分不明显的不建议创建索引

如 user 表中的性别字段，可以明显区分的才建议创建索引，如身份证等字段。

```sql
select * from user where sex=1
```
原因：性别只有男，女，每次过滤掉的数据很少，不宜使用索引。

经验上，能过滤80%数据时就可以使用索引。对于订单状态，如果状态值很少，不宜使用索引，如果状态值很多，能够过滤大量数据，则应该建立索引。

#### 字段的默认值不要为 null

这样会带来和预期不一致的查询结果，建议参考注意事项。

#### 在属性上进行计算不能命中索引

```sql
select * from order where YEAR(date) < = '2017'
```

即使date上建立了索引，也会全表扫描，可优化为值计算：

```sql
select * from order where date < = CURDATE()
```

#### 复合索引最左前缀

用户中心建立了(login_name, passwd)的复合索引

```sql
select * from user where login_name=? and passwd=?

select * from user where passwd=? and login_name=?

```

但是使用

```sql
select * from user where passwd=?
```
不能命中索引，不满足复合索引最左前缀

#### 如果明确知道只有一条记录返回

```sql
select name from user where username='xxxx' limit 1
```
提高效率，可以让数据库停止游标移动，停止全表扫描。

#### 强制类型转换会全表扫描

```sql
select * from user where phone=13800001234
```
这样虽然可以查出数据，但会导致索引失效。

需要修改为

```sql
select * from user where phone='13800001234'
```

#### 把计算放到业务层而不是数据库层，除了节省数据的CPU，还有意想不到的查询缓存优化效果


## 参考引用

58沈剑 架构师之路

