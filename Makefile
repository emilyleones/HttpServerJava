
build-image:
	docker build -t http-server .

run:
	ROOT_DIR=/
	docker run -it -e ROOT_DIR=${ROOT_DIR} -v=${ROOT_DIR}:${ROOT_DIR} -p 127.0.0.1:8080:8080 http-server