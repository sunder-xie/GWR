--table
CREATE TABLE WEBDATA.REPEATING_PAYORDER (	
RECORDID vARCHAR2(50) NOT NULL, 
TRADE_NO VARCHAR2(25), 
PAYSEQNO vARCHAR2(25), 
PAYMETHOD VARCHAR2(30), 
NOTIFY_TIME TIMESTAMP (6), 
STATUS VARCHAR2(10 ), 
AMOUNT NUMBER(5), 
SUCCESS_PAYMETHOD VARCHAR2(30), 
CONFIRM_USER VARCHAR2(50 ), 
CONFIRM_TIME TIMESTAMP (6), 
CONSTRAINT REPEATING_PAYORDER_PK PRIMARY KEY (RECORDID) using index tablespace tbs_index
);

grant select,insert,update on REPEATING_PAYORDER to shanghai;
--view
CREATE OR REPLACE VIEW VIEW_REPEATING_PAYORDER AS
SELECT RECORDID,TRADE_NO,PAYSEQNO,PAYMETHOD,NOTIFY_TIME,STATUS,AMOUNT,SUCCESS_PAYMETHOD,CONFIRM_USER,CONFIRM_TIME 
FROM WEBDATA.REPEATING_PAYORDER;

grant select on VIEW_REPEATING_PAYORDER to baobiao;

--member id query
select recordid, mobile from member where mobile in ('13381971635','13818812026','13761015109','15021763765','18666130269','15821481750','13764478527','15201972328','18917707033','13482163621','13917901075','13661536594','13381151580','18917705859','13951059000','13916558593','18917608051','18917706588','15216715720','13581999919','15618677550','13621934630','13917302507','18710600512','13818799239','18101880926','18017766825','18621553979','13774451767','18616649980','13917808754','18917706566','18916666915','13761043477','13916461784','13918656639','13681911147','13611665266','13611681240','13482130003','13795454972','13917221477','13816896876','','13764213688','13761928015','13391122901','13764051427','18616902795','18621553996','13651886575','13918103604');


--table
CREATE TABLE WEBDATA.PAY_METHOD (	
PAYMETHOD vARCHAR2(30) NOT NULL, 
PAYMETHOD_TEXT vARCHAR2(50),
FLAG VARCHAR2(10 ),
CONSTRAINT PAY_METHOD_PK PRIMARY KEY (PAYMETHOD) using index tablespace tbs_index
);
grant select on PAY_METHOD to shanghai;

--pay_method init
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('gewaPay','�������');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('sysPay','ϵͳ');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('elecardPay','����ȯ');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('ccardPay','���߳�ֵ��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('lakalaPay','������');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('abcPay','ũ�к���');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('wcanPay','΢�ܿƼ����ֶһ�');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('partnerPay','������');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('okcardPay','����OK');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('spsdoPay','ʢ�����');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('directPay','֧����PC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('pnrPay','�㸶����PC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('cmPay','�ƶ��ֻ�֧��PC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('tempusPay','�ڸ�ͨPC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('spsdo2Pay','ʢ��ͨPC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('chinaPay','��������');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('china2Pay','ChinapayPC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('srcbPay','Chinapayũ����');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('unionPay','unionPay����֧��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('unionPayFast','unionPayFast�������֧��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('unionPayFast_activity_js','unionPayFast���ջ');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('unionPayFast_activity_bj','������֤2.0�����');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('unionPay_js','unionPay����');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('unionPay_activity','unionPay�');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('unionPay_activity_js','unionPay���ջ');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('bcPay','����ֱ��PC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('spdPay','�ַ�ֱ��PC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('cmbPay','����ֱ��PC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('gdbPay','�㷢ֱ��PC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('psbcPay','�ʴ�ֱ��PC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('hzbankPay','��������');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('ccbposPay','����ֱ��PC��-���ÿ�');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('jsbChina','��������ֱ��PC��-���ÿ�');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('spdPay_activity','�ַ�ֱ��PC��-�');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('bocPay','�й�����ֱ��PC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('bocWapPay','�й�����ֱ��WAP��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('bocAgrmtPay','�й�����Э��֧��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('umPay','�ƶ�����֧��(��������)');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('umPay_sh','�ƶ�����֧��(��������)_�Ϻ�');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('telecomPay','���Ź̻�����֧��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('telecomMobilePay','�����ֻ�����֧��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('yagaoPay','�Ÿ߿�֧��(�������)');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('onetownPay','һ�ǿ�֧��(�»���ý)');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('aliwapPay','֧�����ֻ���-WAP֧��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('cmwapPay','�ƶ��ֻ�֧���ֻ���-WAP֧��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('cmbwapPay','����ֱ���ֻ���');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('spdWapPay','�ַ�ֱ���ֻ���-WAP');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('cmSmartPay','�ƶ��ֻ�֧����׿��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('spdWapPay_activity','�ַ�ֱ���ֻ���-�');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('chinaSmartMobilePay','�����ֻ�����֧��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('chinaSmartJsPay','���������ֻ���-���������յ�');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('aliSmartMobilePay','֧�����ֻ���-��ȫ֧��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('hzwapPay','��������WAP');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('yeePay','�ױ�֧��PC��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('payecoDNAPay','����DNA֧��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('sdoPay','ʢ�����');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('tenPay','�Ƹ�ͨ');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('ipsPay','��ѶPC��-���ÿ�֧��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('bcwapPay','��ͨWAP');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('allinPay','ͨ��');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('alibankPay','֧������������WAP');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('handwapPay','����WAP');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('handwebPay','����WEB');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('pnrfastPay','�㸶���֧��--�������ÿ�');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('pnrfastPay2','�㸶���֧��--�������ÿ�');
insert into webdata.PAY_METHOD (PAYMETHOD,PAYMETHOD_TEXT) values('pnrfastabcPay','�㸶���֧��--ũ�����ÿ�');

insert into webdata.PAY_METHOD( PAYMETHOD,PAYMETHOD_TEXT) values('icbcPay','��������ֱ��֧��PC��');
insert into webdata.PAY_METHOD( PAYMETHOD,PAYMETHOD_TEXT) values('njcbPay','�Ͼ�����ֱ��֧��PC��');
insert into webdata.PAY_METHOD( PAYMETHOD,PAYMETHOD_TEXT) values('abchinaPay','ũҵ����ֱ��֧��PC��');
