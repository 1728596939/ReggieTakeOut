create table reggie.orders
(
    id              bigint         not null comment '主键'
        primary key,
    number          varchar(50)    null comment '订单号',
    status          int default 1  not null comment '订单状态 1待付款，2待派送，3已派送，4已完成，5已取消',
    user_id         bigint         not null comment '下单用户',
    address_book_id bigint         not null comment '地址id',
    order_time      datetime       not null comment '下单时间',
    checkout_time   datetime       not null comment '结账时间',
    pay_method      int default 1  not null comment '支付方式 1微信,2支付宝',
    amount          decimal(10, 2) not null comment '实收金额',
    remark          varchar(100)   null comment '备注',
    phone           varchar(255)   null,
    address         varchar(255)   null,
    user_name       varchar(255)   null,
    consignee       varchar(255)   null
)
    comment '订单表' collate = utf8_bin;

