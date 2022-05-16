CREATE DATABASE PrivacyServiceDB;
GO

USE PrivacyServiceDB;

-- ************************************** [Manager]
CREATE TABLE [Manager]
(
 [Email] varchar(50) NOT NULL ,
 [Name]  varchar(50) NULL ,


 CONSTRAINT [PK_65] PRIMARY KEY CLUSTERED ([Email] ASC)
);
GO

-- ************************************** [Service]
CREATE TABLE [Service]
(
 [id]        varchar(100) NOT NULL ,
 [Name]      varchar(50) NULL ,
 [ManagerID] varchar(50) NOT NULL ,


 CONSTRAINT [PK_5] PRIMARY KEY CLUSTERED ([id] ASC),
 CONSTRAINT [FK_67] FOREIGN KEY ([ManagerID])  REFERENCES [Manager]([Email])
);
GO

-- ************************************** [Student]
CREATE TABLE [Student]
(
 [Email] varchar(50) NOT NULL ,
 [Name]  varchar(50) NULL ,


 CONSTRAINT [PK_19] PRIMARY KEY CLUSTERED ([Email] ASC)
);
GO

-- ************************************** [Purpose]
CREATE TABLE [Purpose]
(
 [ID]          int NOT NULL ,
 [Title]       varchar(50) NOT NULL ,
 [Description] varchar(1000) NOT NULL ,
 [Version]     int NOT NULL ,


 CONSTRAINT [PK_37] PRIMARY KEY CLUSTERED ([ID] ASC)
);
GO

-- ************************************** [Course]
CREATE TABLE [Course]
(
 [id]          varchar(50) NOT NULL ,
 [ServiceID]   varchar(100) NOT NULL ,
 [Name]        varchar(50) NULL ,
 [Description] varchar(500) NULL ,


 CONSTRAINT [PK_8] PRIMARY KEY CLUSTERED ([id] ASC, [ServiceID] ASC),
 CONSTRAINT [CourseService] FOREIGN KEY ([ServiceID])  REFERENCES [Service]([id])
);
GO

-- ************************************** [StudentInCourse]
CREATE TABLE [StudentInCourse]
(
 [StudentID] varchar(50) NOT NULL ,
 [CourseID]  varchar(50) NOT NULL ,
 [ServiceID] varchar(100) NOT NULL ,
 [Pseudonym] varchar(70) NOT NULL ,


 CONSTRAINT [PK_29] PRIMARY KEY CLUSTERED ([StudentID] ASC, [CourseID] ASC, [ServiceID] ASC),
 CONSTRAINT [SICCourse] FOREIGN KEY ([CourseID], [ServiceID])  REFERENCES [Course]([id], [ServiceID]),
 CONSTRAINT [SICStudent] FOREIGN KEY ([StudentID])  REFERENCES [Student]([Email])
);
GO

-- ************************************** [PurposeInCourse]
CREATE TABLE [PurposeInCourse]
(
 [CourseID]  varchar(50) NOT NULL ,
 [ServiceID] varchar(100) NOT NULL ,
 [PurposeID] int NOT NULL ,


 CONSTRAINT [PK_46] PRIMARY KEY CLUSTERED ([CourseID] ASC, [ServiceID] ASC, [PurposeID] ASC),
 CONSTRAINT [PICCourse] FOREIGN KEY ([CourseID], [ServiceID])  REFERENCES [Course]([id], [ServiceID]),
 CONSTRAINT [PICPurpose] FOREIGN KEY ([PurposeID])  REFERENCES [Purpose]([ID])
);
GO

-- ************************************** [Consent] TODO: REMOVE!
CREATE TABLE [Consent]
(
 [StudentID]   varchar(50) NOT NULL ,
 [CourseID]    varchar(50) NOT NULL ,
 [ServiceID]   varchar(100) NOT NULL ,
 [CourseID_1]  varchar(50) NOT NULL ,
 [ServiceID_1] varchar(100) NOT NULL ,
 [PurposeID]   int NOT NULL ,
 [Timestamp]   datetime NOT NULL ,


 CONSTRAINT [PK_56] PRIMARY KEY CLUSTERED ([StudentID] ASC, [CourseID] ASC, [ServiceID] ASC, [CourseID_1] ASC, [ServiceID_1] ASC, [PurposeID] ASC),
 CONSTRAINT [ConsentPIC] FOREIGN KEY ([CourseID_1], [ServiceID_1], [PurposeID])  REFERENCES [PurposeInCourse]([CourseID], [ServiceID], [PurposeID]),
 CONSTRAINT [ConsentSIC] FOREIGN KEY ([StudentID], [CourseID], [ServiceID])  REFERENCES [StudentInCourse]([StudentID], [CourseID], [ServiceID])
);
GO