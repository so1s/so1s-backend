INSERT INTO DEPLOYMENT_STRATEGY (name) VALUES ('rolling');

INSERT INTO LIBRARY (name) VALUES ('tensorflow');
INSERT INTO LIBRARY (name) VALUES ('torch');
INSERT INTO LIBRARY (name) VALUES ('sklearn');

INSERT INTO MODEL (CREATED_ON , UPDATED_ON, NAME , LIBRARY_ID) VALUES (now(), now(), 'testModel', 1);
INSERT INTO MODEL_METADATA (CREATED_ON, UPDATED_ON, FILE_NAME, INPUT_DTYPE, INPUT_SHAPE, OUTPUT_DTYPE, OUTPUT_SHAPE, STATUS, URL, VERSION, MODEL_ID) VALUES (now(), now(), 'test_file1', 'float32', '(10,)', 'float32', '(1,)', 'pending', 'http://s3.test.com/1/1', 'v1', 1);
INSERT INTO MODEL_METADATA (CREATED_ON, UPDATED_ON, FILE_NAME, INPUT_DTYPE, INPUT_SHAPE, OUTPUT_DTYPE, OUTPUT_SHAPE, STATUS, URL, VERSION, MODEL_ID) VALUES (now(), now(), 'test_file2', 'float32', '(5,)', 'float32', '(1,)', 'pending', 'http://s3.test.com/1/2', 'v2', 1);
INSERT INTO MODEL (CREATED_ON , UPDATED_ON, NAME , LIBRARY_ID) VALUES (now(), now(), 'modelTest', 2);
INSERT INTO MODEL_METADATA (CREATED_ON, UPDATED_ON, FILE_NAME, INPUT_DTYPE, INPUT_SHAPE, OUTPUT_DTYPE, OUTPUT_SHAPE, STATUS, URL, VERSION, MODEL_ID) VALUES (now(), now(), 'file_test1', 'float32', '(2,)', 'float32', '(1,)', 'pending', 'http://s3.test.com/2/1', 'v1', 2);
INSERT INTO MODEL_METADATA (CREATED_ON, UPDATED_ON, FILE_NAME, INPUT_DTYPE, INPUT_SHAPE, OUTPUT_DTYPE, OUTPUT_SHAPE, STATUS, URL, VERSION, MODEL_ID) VALUES (now(), now(), 'file_test2', 'float32', '(2,)', 'float32', '(1,)', 'pending', 'http://s3.test.com/2/2', 'v2', 2);
INSERT INTO MODEL_METADATA (CREATED_ON, UPDATED_ON, FILE_NAME, INPUT_DTYPE, INPUT_SHAPE, OUTPUT_DTYPE, OUTPUT_SHAPE, STATUS, URL, VERSION, MODEL_ID) VALUES (now(), now(), 'file_test3', 'float32', '(2,)', 'float32', '(1,)', 'pending', 'http://s3.test.com/2/3', 'v3', 2);
INSERT INTO MODEL_METADATA (CREATED_ON, UPDATED_ON, FILE_NAME, INPUT_DTYPE, INPUT_SHAPE, OUTPUT_DTYPE, OUTPUT_SHAPE, STATUS, URL, VERSION, MODEL_ID) VALUES (now(), now(), 'file_test4', 'float32', '(2,)', 'float32', '(1,)', 'pending', 'http://s3.test.com/2/4', 'v4', 2);
