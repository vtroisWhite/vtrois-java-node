emojiFilter
==========

## 简介

由于线上mysql字符编码为utf8,不是utf8mb4,导致只能存储长度为3byte的utf字符,存入4byte字符时会报错,所以需要过滤字符串中byte长度大于4的字符

## 作用

如简介所述，过滤byte长度过长的字符

## 说明

运行[EmojiFilterUtil.java](EmojiFilterUtil.java) main方法
运行结果：

```
原始字符="key":"热带鱼🐟童装"😒🤣🎁🐱‍💻😁🤷‍♀️😜✨😂😊😎🎉💋🤞🙌😃❤😍👌😘💕👍🤦‍♀️🤦‍♂️🌹👏💖😢🎶😉✌🤷‍♂️🎂🤳🐱‍👤🐱‍🏍🐱‍🐉🐱‍👓🐱‍🚀✔🤢🤔😆👀💅⏰📊🍨🍯🚦🚧♎♓💫🕖🗯🔹key":{"key":🔥{"key1":"中国🐷","key2":"广👀东省","city":"江门市","district":"蓬江区"}},"deviceIn下😎fo
过滤结果="key":"热带鱼童装"‍‍♀️✨❤‍♀️‍♂️✌‍♂️‍‍‍‍‍✔⏰♎♓key":{"key":{"key1":"中国","key2":"广东省","city":"江门市","district":"蓬江区"}},"deviceIn下fo
原始字符=🤣
过滤结果=
```