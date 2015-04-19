----------------------------------------------------
job_history_query

SELECT top 1000 [OBJECT_KEY]
      ,[SERVICE]
      ,[START_TIME]
      ,[END_TIME]
      ,[EXECUTION_TIME]
      ,[STATUS]
      ,[HAS_ERROR]
      ,[IGNORE_ERROR]
  FROM [dbo].[AL_HISTORY] order by START_TIME desc