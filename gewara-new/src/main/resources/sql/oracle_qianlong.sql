---2012-08-16
---PROGRAM_ITEM_TIME���citycode
alter table webdata.PROGRAM_ITEM_TIME add CITYCODE VARCHAR2 (6);
update webdata.PROGRAM_ITEM_TIME set CITYCODE='310000';
update webdata.OPEN_TIMEITEM set CITYCODE='310000' where citycode is null;
update webdata.MOVIE set playdate = to_char(releasedate,'YYYY-MM-DD') where releasedate is not null;

-- 20120619	CUSTOMER_QUESTION���FEEDBACKTYPE�ֶ�
alter table CUSTOMER_QUESTION add FEEDBACKTYPE VARCHAR2(20) default 'other'; --������������

-- 20120619	SYNCH���IP�ֶ�
alter table SYNCH add IP VARCHAR2(15);  --ͬ��IP

-- 20121011	TICKET_ORDER���RESTATUS�ֶ�
alter table webdata.TICKET_ORDER add RESTATUS VARCHAR2(4);  --����ɾ��״̬

--20121105
alter table webdata.MOVIE add EDITION VARCHAR2(50); --��Ӱ�汾
