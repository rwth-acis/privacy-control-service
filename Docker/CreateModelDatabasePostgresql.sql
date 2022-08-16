CREATE DATABASE PrivacyServiceDB;
 

SET SCHEMA 'PrivacyServiceDB';

-- SQLINES DEMO *** ********************* [DPO]
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE IF NOT EXISTS DPO
(
 email varchar(50) NOT NULL ,


 CONSTRAINT PK_72 PRIMARY KEY (email)
);

-- SQLINES DEMO *** ********************* [Manager]
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE IF NOT EXISTS Manager 
(
 Email varchar(50) NOT NULL ,
 Name  varchar(50) NULL ,


 CONSTRAINT PK_65 PRIMARY KEY (Email)
);

-- SQLINES DEMO *** ********************* [Service]
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE IF NOT EXISTS Service
(
 id        varchar(100) NOT NULL ,
 Name      varchar(50) NULL ,
 ManagerID varchar(50) NOT NULL ,


 CONSTRAINT PK_5 PRIMARY KEY (id),
 CONSTRAINT FK_67 FOREIGN KEY (ManagerID)  REFERENCES Manager(Email)
);

-- SQLINES DEMO *** ********************* [Student]
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE IF NOT EXISTS Student
(
 Email varchar(50) NOT NULL ,
 Name  varchar(50) NULL ,


 CONSTRAINT PK_19 PRIMARY KEY (Email)
);

-- SQLINES DEMO *** ********************* [Purpose]
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE IF NOT EXISTS Purpose
(
 ID          int NOT NULL ,
 Title       varchar(50) NOT NULL ,
 Description varchar(1000) NOT NULL ,
 Version     int NOT NULL ,


 CONSTRAINT PK_37 PRIMARY KEY (ID)
);

-- SQLINES DEMO *** ********************* [Course]
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE IF NOT EXISTS Course
(
 id          varchar(50) NOT NULL ,
 ServiceID   varchar(100) NOT NULL ,
 Name        varchar(50) NULL ,
 Description varchar(500) NULL ,


 CONSTRAINT PK_8 PRIMARY KEY (id, ServiceID),
 CONSTRAINT CourseService FOREIGN KEY (ServiceID)  REFERENCES Service(id)
);

-- SQLINES DEMO *** ********************* [StudentInCourse]
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE IF NOT EXISTS StudentInCourse
(
 StudentID varchar(50) NOT NULL ,
 CourseID  varchar(50) NOT NULL ,
 ServiceID varchar(100) NOT NULL ,
 Pseudonym varchar(70) NOT NULL ,


 CONSTRAINT PK_29 PRIMARY KEY (StudentID, CourseID, ServiceID),
 CONSTRAINT SICCourse FOREIGN KEY (CourseID, ServiceID)  REFERENCES Course(id, ServiceID),
 CONSTRAINT SICStudent FOREIGN KEY (StudentID)  REFERENCES Student(Email)
);

-- SQLINES DEMO *** ********************* [PurposeInCourse]
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE IF NOT EXISTS PurposeInCourse
(
 CourseID  varchar(50) NOT NULL ,
 ServiceID varchar(100) NOT NULL ,
 PurposeID int NOT NULL ,


 CONSTRAINT PK_46 PRIMARY KEY (CourseID, ServiceID, PurposeID),
 CONSTRAINT PICCourse FOREIGN KEY (CourseID, ServiceID)  REFERENCES Course(id, ServiceID),
 CONSTRAINT PICPurpose FOREIGN KEY (PurposeID)  REFERENCES Purpose(ID)
);

-- SQLINES DEMO *** ********************* [Consent] TODO: REMOVE!
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE IF NOT EXISTS Consent
(
 StudentID   varchar(50) NOT NULL ,
 CourseID    varchar(50) NOT NULL ,
 ServiceID   varchar(100) NOT NULL ,
 CourseID_1  varchar(50) NOT NULL ,
 ServiceID_1 varchar(100) NOT NULL ,
 PurposeID   int NOT NULL ,
 Timestamp   timestamp(3) NOT NULL ,


 CONSTRAINT PK_56 PRIMARY KEY (StudentID, CourseID, ServiceID, CourseID_1, ServiceID_1, PurposeID),
 CONSTRAINT ConsentPIC FOREIGN KEY (CourseID_1, ServiceID_1, PurposeID)  REFERENCES PurposeInCourse(CourseID, ServiceID, PurposeID),
 CONSTRAINT ConsentSIC FOREIGN KEY (StudentID, CourseID, ServiceID)  REFERENCES StudentInCourse(StudentID, CourseID, ServiceID)
);
