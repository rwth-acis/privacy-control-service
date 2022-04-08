CREATE DATABASE PrivacyService;
GO

USE PrivacyService;
GO

CREATE TABLE [Service]
(
 [id]   int NOT NULL ,
 [Name] varchar(50) NOT NULL ,


 CONSTRAINT [PK_5] PRIMARY KEY CLUSTERED ([id] ASC)
);
GO

CREATE TABLE [Course]
(
 [id]          int NOT NULL ,
 [ServiceID]   int NOT NULL ,
 [Name]        varchar(50) NOT NULL ,
 [Description] varchar(500) ,


 CONSTRAINT [PK_8] PRIMARY KEY CLUSTERED ([id] ASC, [ServiceID] ASC),
 CONSTRAINT [CourseService] FOREIGN KEY ([ServiceID])  REFERENCES [Service]([id])
);
GO

CREATE TABLE [Student]
(
 [id]    int NOT NULL ,
 [Name]  varchar(50) NOT NULL ,
 [Email] varchar(50) NOT NULL ,


 CONSTRAINT [PK_19] PRIMARY KEY CLUSTERED ([id] ASC)
);
GO

CREATE TABLE [StudentInCourse]
(
 [StudentID] int NOT NULL ,
 [CourseID]  int NOT NULL ,
 [ServiceID] int NOT NULL ,
 [Pseudonym] varchar(70) NOT NULL ,


 CONSTRAINT [PK_29] PRIMARY KEY CLUSTERED ([StudentID] ASC, [CourseID] ASC, [ServiceID] ASC),
 CONSTRAINT [SICCourse] FOREIGN KEY ([CourseID], [ServiceID])  REFERENCES [Course]([id], [ServiceID]),
 CONSTRAINT [SICStudent] FOREIGN KEY ([StudentID])  REFERENCES [Student]([id])
);
GO

CREATE TABLE [Purpose]
(
 [ID]          int NOT NULL ,
 [Title]       varchar(50) NOT NULL ,
 [Description] varchar(1000) NOT NULL ,
 [Version]     int NOT NULL ,


 CONSTRAINT [PK_37] PRIMARY KEY CLUSTERED ([ID] ASC)
);
GO

CREATE TABLE [PurposeInCourse]
(
 [CourseID]  int NOT NULL ,
 [ServiceID] int NOT NULL ,
 [PurposeID] int NOT NULL ,


 CONSTRAINT [PK_46] PRIMARY KEY CLUSTERED ([CourseID] ASC, [ServiceID] ASC, [PurposeID] ASC),
 CONSTRAINT [PICCourse] FOREIGN KEY ([CourseID], [ServiceID])  REFERENCES [Course]([id], [ServiceID]),
 CONSTRAINT [PICPurpose] FOREIGN KEY ([PurposeID])  REFERENCES [Purpose]([ID])
);
GO

CREATE TABLE [Consent]
(
 [StudentID]   int NOT NULL ,
 [CourseID]    int NOT NULL ,
 [ServiceID]   int NOT NULL ,
 [CourseID_1]  int NOT NULL ,
 [ServiceID_1] int NOT NULL ,
 [PurposeID]   int NOT NULL ,
 [Timestamp]   datetime NOT NULL ,


 CONSTRAINT [PK_56] PRIMARY KEY CLUSTERED ([StudentID] ASC, [CourseID] ASC, [ServiceID] ASC, [CourseID_1] ASC, [ServiceID_1] ASC, [PurposeID] ASC),
 CONSTRAINT [ConsentPIC] FOREIGN KEY ([CourseID_1], [ServiceID_1], [PurposeID])  REFERENCES [PurposeInCourse]([CourseID], [ServiceID], [PurposeID]),
 CONSTRAINT [ConsentSIC] FOREIGN KEY ([StudentID], [CourseID], [ServiceID])  REFERENCES [StudentInCourse]([StudentID], [CourseID], [ServiceID])
);
GO