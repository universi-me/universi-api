FROM azul/zulu-openjdk-alpine:17-jre

ADD target/*.jar /app.jar

ADD entrypoint.sh entrypoint.sh

CMD ["entrypoint.sh"]
ENTRYPOINT ["sh"]