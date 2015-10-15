drop table if exists enterprises;
create table enterprises (
    id integer primary key autoincrement,
    name text not null,
    category text not null,
    img text not null
);

drop table if exists quejas;
create table quejas (
    id integer primary key autoincrement,
    complain text not null,
    enterprise_id integer not null
);
