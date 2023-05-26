---------------------
-- Insert exercise --
----------------------
INSERT INTO exercise (title, group_id, inactivate)VALUES('Padrões de projeto:', 1, false);
--------------------------
--insert queston 1--
--------------------
INSERT INTO feedback (link, feedback_text)
VALUES ('https://exemplo.com/dica-de-estudo-padrao3', 'Dica de estudo: Saiba como o padrão de projeto Observer pode ser aplicado em sistemas de eventos.');

INSERT INTO question  (title, feedback_id, user_create_id)
VALUES ('QUAL É A IDEIA CENTRAL DO PADRÃO DE PROJETO OBSERVER?', 1, 1);


INSERT INTO exercise_question (exercise_id, question_id) VALUES(1, 1);

-- Primeira alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('A separação de um objeto em partes menores para facilitar a manutenção.', false, 1);

-- Segunda alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('O uso de herança múltipla para criar uma hierarquia de classes flexível.', false, 1);

-- Terceira alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('A definição de uma interface comum para a comunicação entre objetos.', true, 1);

-- Quarta alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('A aplicação de regras de validação em um formulário web.', false, 1);

-- Quinta alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('A utilização de métodos estáticos para compartilhar funcionalidades entre objetos.', false, 1);

-------------------------------------------------------
--insert question 2
---------------------------
INSERT INTO feedback (link, feedback_text)
VALUES ('https://exemplo.com/dica-de-estudo-padrao2', 'Dica de estudo: Conheça os princípios do padrão de projeto MVC.');

INSERT INTO question (title, feedback_id, user_create_id)
VALUES ('O que significa o acrônimo MVC e como funciona?', 2, 1);

INSERT INTO exercise_question (exercise_id, question_id) VALUES(1, 2);

-- Primeira alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('MVC significa Model-View-Controller e é um padrão de projeto utilizado para separar a lógica de negócio, a apresentação e a interação com o usuário em componentes distintos.', true, 2);

-- Segunda alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('MVC significa Main-View-Component e é uma abordagem para construir interfaces de usuário interativas.', false, 2);

-- Terceira alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('MVC significa Modern-View-Control e é uma metodologia para otimizar o desempenho de aplicações web.', false, 2);

-- Quarta alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('MVC significa Model-Validation-Caching e é uma técnica para melhorar a segurança e a velocidade de acesso aos dados.', false, 2);

-- Quinta alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('MVC significa Mobile-View-Configuration e é um modelo de desenvolvimento para aplicativos móveis.', false, 2);

---------------------
--insert question 3 -
---------------------
INSERT INTO feedback (link, feedback_text)
VALUES ('https://exemplo.com/dica-de-estudo-padrao4', 'Dica de estudo: Entenda as diferenças entre o padrão de projeto Factory Method e Abstract Factory.');

INSERT INTO question (title, feedback_id, user_create_id)
VALUES ('Quais são as principais diferenças entre Factory Method e Abstract Factory?', 3, 1);

INSERT INTO exercise_question (exercise_id, question_id) VALUES(1, 3);

-- Primeira alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('O Factory Method cria objetos de uma única classe, enquanto o Abstract Factory cria objetos de uma família de classes relacionadas.', true, 3);

-- Segunda alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('O Factory Method permite a criação de objetos sem especificar a classe concreta, enquanto o Abstract Factory fornece uma interface para criar famílias de objetos relacionados.', false, 3);

-- Terceira alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('O Factory Method é um padrão de projeto criacional, enquanto o Abstract Factory é um padrão de projeto estrutural.', false, 3);

-- Quarta alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('O Factory Method é baseado em herança, enquanto o Abstract Factory é baseado em composição de objetos.', false, 3);

-- Quinta alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('O Factory Method é mais flexível e permite a extensão de subclasses para criar objetos, enquanto o Abstract Factory é mais rígido e requer a definição de uma hierarquia de fábricas.', false, 3);


---------------------
--insert question 4 -
---------------------
INSERT INTO feedback (link, feedback_text)
VALUES ('https://exemplo.com/dica-de-estudo-padrao5', 'Dica de estudo: Aprenda a utilizar o padrão de projeto Singleton de forma correta e segura.');

INSERT INTO question (title, feedback_id, user_create_id)
VALUES ('Quais são as vantagens e desvantagens do uso do padrão de projeto Singleton?', 4, 1);

INSERT INTO exercise_question (exercise_id, question_id) VALUES(1, 4);

-- Primeira alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('O padrão Singleton garante que uma classe tenha apenas uma instância e fornece um ponto global de acesso a essa instância.', true, 4);

-- Segunda alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('O padrão Singleton é adequado para situações em que várias instâncias da mesma classe devem ser criadas.', false, 4);

-- Terceira alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('O padrão Singleton é uma técnica para criar objetos imutáveis.', false, 4);

-- Quarta alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('O padrão Singleton utiliza herança para criar objetos únicos.', false, 4);

-- Quinta alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('O padrão Singleton é uma solução para implementar o polimorfismo em uma classe.', false, 4);


---------------------
--insert question 5 -
---------------------
-- Inserção do feedback relacionado à pergunta
INSERT INTO feedback (link, feedback_text)
VALUES ('https://exemplo.com/dica-de-estudo-padroes-de-projeto', 'Dica de estudo: Explore os padrões de projeto para entender como cada um pode ser aplicado em diferentes cenários de desenvolvimento de software.');

-- Pergunta fictícia sobre padrões de projeto
INSERT INTO question (title, feedback_id, user_create_id)
VALUES ('Qual dos padrões de projeto a seguir é mais adequado para implementar uma lógica de negócio complexa em um sistema?', 5, 1);

-- Primeira alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('Singleton', false, 5);

-- Segunda alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('Observer', false, 5);

-- Terceira alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('Facade', true, 5);

-- Quarta alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('Decorator', false, 5);

-- Quinta alternativa
INSERT INTO alternative (title, correct, question_id)
VALUES ('Command', false, 5);

