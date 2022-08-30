INSERT INTO DEPLOYMENT_STRATEGY (name) VALUES ('rolling');

INSERT INTO LIBRARY (name) VALUES ('tensorflow');
INSERT INTO LIBRARY (name) VALUES ('torch');
INSERT INTO LIBRARY (name) VALUES ('sklearn');

INSERT INTO MODEL (CREATED_ON, UPDATED_ON, NAME, LIBRARY_ID) VALUES (now(), now(), 'tensorflowModel', 1);
INSERT INTO MODEL (CREATED_ON, UPDATED_ON, NAME, LIBRARY_ID) VALUES (now(), now(), 'torchModel', 2);
INSERT INTO MODEL_METADATA (CREATED_ON, UPDATED_ON, FILE_NAME, INPUT_DTYPE, INPUT_SHAPE, OUTPUT_DTYPE, OUTPUT_SHAPE, STATUS, URL, VERSION, MODEL_ID) VALUES (now(), now(), 'tensorflowModel.h5', 'float32', '(10,)', 'float32', '(1,)', 'SUCCEEDED', 'http://s3.so1s.com/tensorflowModel.h5', 'v1', 1);
INSERT INTO MODEL_METADATA (CREATED_ON, UPDATED_ON, FILE_NAME, INPUT_DTYPE, INPUT_SHAPE, OUTPUT_DTYPE, OUTPUT_SHAPE, STATUS, URL, VERSION, MODEL_ID) VALUES (now(), now(), 'tensorflowModel.h5', 'float32', '(10,)', 'float32', '(1,)', 'SUCCEEDED', 'http://s3.so1s.com/tensorflowModel.h5', 'v2', 1);
INSERT INTO MODEL_METADATA (CREATED_ON, UPDATED_ON, FILE_NAME, INPUT_DTYPE, INPUT_SHAPE, OUTPUT_DTYPE, OUTPUT_SHAPE, STATUS, URL, VERSION, MODEL_ID) VALUES (now(), now(), 'torchModel.h5', 'float32', '(10,)', 'float32', '(1,)', 'SUCCEEDED', 'http://s3.so1s.com/torchModel.h5', 'v1', 2);
INSERT INTO MODEL_METADATA (CREATED_ON, UPDATED_ON, FILE_NAME, INPUT_DTYPE, INPUT_SHAPE, OUTPUT_DTYPE, OUTPUT_SHAPE, STATUS, URL, VERSION, MODEL_ID) VALUES (now(), now(), 'torchModel.h5', 'float32', '(10,)', 'float32', '(1,)', 'FAILED', 'http://s3.so1s.com/torchModel.h5', 'v2', 2);
INSERT INTO MODEL_METADATA (CREATED_ON, UPDATED_ON, FILE_NAME, INPUT_DTYPE, INPUT_SHAPE, OUTPUT_DTYPE, OUTPUT_SHAPE, STATUS, URL, VERSION, MODEL_ID) VALUES (now(), now(), 'torchModel.h5', 'float32', '(10,)', 'float32', '(1,)', 'SUCCEEDED', 'http://s3.so1s.com/torchModel.h5', 'v3', 2);

INSERT INTO RESOURCE (CREATED_ON, UPDATED_ON, CPU, CPU_LIMIT, GPU, GPU_LIMIT, MEMORY, MEMORY_LIMIT) VALUES (now(), now(), '1', '2', '0', '0', '1Gi', '2Gi');
INSERT INTO RESOURCE (CREATED_ON, UPDATED_ON, CPU, CPU_LIMIT, GPU, GPU_LIMIT, MEMORY, MEMORY_LIMIT) VALUES (now(), now(), '1', '1', '0', '0', '500Mi', '1Gi');

INSERT INTO DEPLOYMENT (CREATED_ON, UPDATED_ON, NAME, STATUS, DEPLOYMENT_STRATEGY_ID, MODEL_METADATA_ID, RESOURCE_ID) VALUES (now(), now(), 'tensorflowModel1Deploy', 'RUNNING', 1, 1, 1);
INSERT INTO DEPLOYMENT (CREATED_ON, UPDATED_ON, NAME, STATUS, DEPLOYMENT_STRATEGY_ID, MODEL_METADATA_ID, RESOURCE_ID) VALUES (now(), now(), 'tensorflowModel2Deploy', 'PEDNING', 1, 2, 1);
INSERT INTO DEPLOYMENT (CREATED_ON, UPDATED_ON, NAME, STATUS, DEPLOYMENT_STRATEGY_ID, MODEL_METADATA_ID, RESOURCE_ID) VALUES (now(), now(), 'torchModel2Deploy1', 'RUNNING', 1, 4, 2);
