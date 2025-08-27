create database hms;
use hms;

create table doctor(Id int primary key auto_increment,Name varchar(100),Speciality varchar(100));
create table patient(p_id int primary key auto_increment,p_Name varchar(100),p_Age int,p_Gender varchar(50),p_Contact varchar(20));
create table appointment(a_id int primary key auto_increment,patient_id int,doctor_id int,start_ts datetime,Status varchar(50),
foreign key(patient_id) references patient(p_id),foreign key(doctor_id) references doctor(Id));
create table admission(ad_id int primary key auto_increment,patient_id int,Ward varchar(50),admit_ts datetime,discharge_ts datetime,
foreign key(patient_id) references patient(p_id));
create table bill(b_id int primary key auto_increment,patient_id int,Total double,Paid double,Status varchar(50),foreign key(patient_id) references patient(p_id));
create table bill_item(bi_id int primary key auto_increment,bill_id int,`desc` text,amount double,
foreign key(bill_id) references bill(b_id));

select * from doctor;
select * from patient;
select * from appointment;
select * from admission;
select * from bill;
select * from bill_item;