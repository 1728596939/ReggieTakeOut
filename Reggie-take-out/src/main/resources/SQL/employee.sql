create table reggie.employee
(
    id          bigint        not null comment '主键'
        primary key,
    name        varchar(32)   not null comment '姓名',
    username    varchar(32)   not null comment '用户名',
    password    varchar(64)   not null comment '密码',
    phone       varchar(11)   not null comment '手机号',
    sex         varchar(2)    not null comment '性别',
    id_number   varchar(18)   not null comment '身份证号',
    status      int default 1 not null comment '状态 0:禁用，1:正常',
    create_time datetime      not null comment '创建时间',
    update_time datetime      not null comment '更新时间',
    create_user bigint        not null comment '创建人',
    update_user bigint        not null comment '修改人',
    constraint idx_username
        unique (username)
)
    comment '员工信息' collate = utf8_bin;

