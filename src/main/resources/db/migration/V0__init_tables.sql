create table question
(
    id       serial       not null
        constraint questions_pkey
            primary key,
    text     varchar(250) not null,
    answer   varchar(250) not null,
);