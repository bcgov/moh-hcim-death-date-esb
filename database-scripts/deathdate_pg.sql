-- Title: Create DeathDate Postgres database
-- Author: Arlo Watts
-- Date: 26-Jun-2023

-- Prerequisites: a database called "registries"

create schema if not exists esbdeath;

-- Delete user
drop role if exists role_esb_death;

-- Create the user role_esb_death
create user role_esb_death superuser;

-- Set the schema in the search path for the user
alter role role_esb_death in database registries set search_path to esbdeath;

-- Set the schema in the search path for the current session
set search_path to esbdeath;

-- Clean tables
alter table if exists event_message drop constraint if exists fk_event_message_transaction_event_id;
drop sequence if exists affctd_prty_affctd_prty_id_seq;
drop sequence if exists xn_evnt_xn_evnt_id_seq;
drop sequence if exists event_msg_event_msg_id_seq;

drop table if exists transaction_info;
drop table if exists transaction_event;
drop table if exists event_message;
drop table if exists affected_party;
drop table if exists camel_messageprocessed;
drop table if exists sequence;

-- Create tables
create table transaction_info (transaction_id varchar(255) not null, organization varchar(255), server varchar(255), source_system varchar(255), type varchar(255), user_id varchar(255), primary key (transaction_id));
create table transaction_event (transaction_event_id integer not null, event_time timestamp, message_id varchar(255), transaction_id varchar(255), type varchar(255), primary key (transaction_event_id));
create table event_message (event_message_id integer not null, error_code varchar(255), error_level varchar(255), message_text varchar(500), transaction_event_id integer, primary key (event_message_id));
create table affected_party (affected_party_id integer not null, identifier varchar(255), identifier_source varchar(255), identifier_type varchar(255), status varchar(255), transaction_id varchar(255), primary key (affected_party_id));
create table camel_messageprocessed (id bigint not null, createdat timestamp, messageid varchar(255), processorname varchar(255), primary key (id));
create table sequence (seq_name varchar(50) not null, seq_count decimal(38), primary key (seq_name));

-- Create sequences and constraints
alter table event_message add constraint fk_event_message_transaction_event_id foreign key (transaction_event_id) references transaction_event (transaction_event_id);
create sequence affctd_prty_affctd_prty_id_seq increment by 50 start with 50;
create sequence xn_evnt_xn_evnt_id_seq increment by 50 start with 50;
create sequence event_msg_event_msg_id_seq increment by 50 start with 50;
