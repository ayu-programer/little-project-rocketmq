
总结收获
    
    1 消费者通过rocketmq来获取消息时，需要定义一个默认的消费者bean对象--DefaultMQPushConsumer
    同时需要设置mq的相关属性，topic，group，nameserver，并且要设置监听器对象
    
    2 消费者通过rocketmq来获取消息时，还需要定义一个监听器对象，这个监听器对象需要实现MessageListener，
    并且在监听器中获取到消息后，转换成具体的实体后，调用service层的方法来去处理
    
    3 对于这种需要通过mq来获取消息的情况，最好是在接收消息及结束的时候打上日志信息。
    
    
    4 对于比较一个结果集是否等于true或false时可以用Boolean.TRUE/  Boolean.FALSE;
    
    5 redis的setnx可以用于判断幂等，如果返回结果是false，表示已经处理过这条记录，如果返回结果为true表示还没处理过