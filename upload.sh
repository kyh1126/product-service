set -x
set -e
ECR_URL=741704045536.dkr.ecr.ap-northeast-2.amazonaws.com
SERVICE_NAME=fn-product

./gradlew clean bootJar
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${ECR_URL}
docker build -t ${SERVICE_NAME} .
docker tag ${SERVICE_NAME}:latest ${ECR_URL}/${SERVICE_NAME}:latest
docker push ${ECR_URL}/${SERVICE_NAME}:latest

aws --region ap-northeast-2 ecs update-service --cluster sfn-dev --service ${SERVICE_NAME} --force-new-deployment >> aws.log

docker image rm ${SERVICE_NAME}
docker image rm ${ECR_URL}/${SERVICE_NAME}