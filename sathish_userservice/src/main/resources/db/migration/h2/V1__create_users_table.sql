create sequence bm_id_seq start with 1 increment by 50;

create table users (
    id bigint default nextval('bm_id_seq') not null,
    text varchar(255) not null,
    created_at timestamp,
    primary key (id)
);
