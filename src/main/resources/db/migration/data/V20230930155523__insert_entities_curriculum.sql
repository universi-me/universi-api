INSERT INTO type_education (id, name, created_at) VALUES
  ('cd6cfd9d-0f53-4e1e-9d8e-349c4e5d2b09', 'Ensino Fundamental', '2023-09-29 14:30:00'),
  ('d261e9b1-42c3-4b5c-ae76-2ebd6e0aa84f', 'Ensino Médio', '2023-09-29 14:45:00'),
  ('e2a5a5a7-5e9d-4e62-9d21-90689c87398a', 'Ensino Superior', '2023-09-29 15:00:00'),
  ('f16e14c1-4e0d-464a-8a61-9565856829ec', 'Pós-Graduação', '2023-09-29 15:15:00');

INSERT INTO institution (id, name, description, created_at) VALUES
  ('cd6cfd9d-0f53-4e1e-9d8e-349c4e5d2b09', 'ACADEMIA DA FORÇA AÉREA', 'Descrição da Academia da Força Aérea', '2023-09-29 14:30:00'),
  ('d261e9b1-42c3-4b5c-ae76-2ebd6e0aa84f', 'CENTRO UNIVERSITÁRIO BARÃO DE MAUÁ', 'Descrição do Centro Universitário Barão de Mauá', '2023-09-29 14:45:00'),
  ('e2a5a5a7-5e9d-4e62-9d21-90689c87398a', 'CENTRO UNIVERSITÁRIO DE ANÁPOLIS', 'Descrição do Centro Universitário de Anápolis', '2023-09-29 15:00:00'),
  ('f16e14c1-4e0d-464a-8a61-9565856829ec', 'CENTRO UNIVERSITÁRIO FILADÉLFIA', 'Descrição do Centro Universitário Filadélfia', '2023-09-29 15:15:00'),
  ('a1e7e876-9d53-4ce9-b9d0-d399edf543ee', 'UNIVERSIDADE FEDERAL DA PARAÍBA', 'Descrição da Universidade Federal da Paraíba', '2023-09-29 15:30:00'),
  ('b2f3f7e1-39a0-4d1c-8f6b-78575e1b6491', 'UNIVERSIDADE FEDERAL DE CAMPINA GRANDE', 'Descrição da Universidade Federal de Campina Grande', '2023-09-29 15:45:00'),
  ('c3d4d3c7-2e54-42af-8b65-9f00f692a17b', 'UNIVERSIDADE ESTADUAL DA PARAÍBA', 'Descrição da Universidade Estadual da Paraíba', '2023-09-29 16:00:00');


INSERT INTO education (id, profile_id, type_education_id, institution_id, start_date, end_date, present_date, created_at) VALUES
  ('cd6cfd9d-0f53-4e1e-9d8e-349c4e5d2b09', '33d16b6f-6b68-4ede-bb6b-7d3e2789804c', 'cd6cfd9d-0f53-4e1e-9d8e-349c4e5d2b09', 'cd6cfd9d-0f53-4e1e-9d8e-349c4e5d2b09', '2023-01-01', '2023-12-31', false, '2023-09-29 14:30:00'),
  ('d261e9b1-42c3-4b5c-ae76-2ebd6e0aa84f', '33d16b6f-6b68-4ede-bb6b-7d3e2789804c', 'd261e9b1-42c3-4b5c-ae76-2ebd6e0aa84f', 'd261e9b1-42c3-4b5c-ae76-2ebd6e0aa84f', '2022-09-01', '2023-06-30', false, '2023-09-29 14:45:00'),
  ('e2a5a5a7-5e9d-4e62-9d21-90689c87398a', '33d16b6f-6b68-4ede-bb6b-7d3e2789804c', 'e2a5a5a7-5e9d-4e62-9d21-90689c87398a', 'e2a5a5a7-5e9d-4e62-9d21-90689c87398a', '2023-03-15', '2023-09-15', true, '2023-09-29 15:00:00'),
  ('f16e14c1-4e0d-464a-8a61-9565856829ec', '05253510-5b6e-4342-b9e3-bccc62759645', 'f16e14c1-4e0d-464a-8a61-9565856829ec', 'f16e14c1-4e0d-464a-8a61-9565856829ec', '2023-02-01', null, true, '2023-09-29 15:15:00'),
  ('a1e7e876-9d53-4ce9-b9d0-d399edf543ee', '05253510-5b6e-4342-b9e3-bccc62759645', 'cd6cfd9d-0f53-4e1e-9d8e-349c4e5d2b09', 'a1e7e876-9d53-4ce9-b9d0-d399edf543ee', '2023-08-01', '2023-12-31', false, '2023-09-29 15:30:00'),
  ('b2f3f7e1-39a0-4d1c-8f6b-78575e1b6491', '69e044fb-2a27-420d-8f3f-2cec3c455639', 'd261e9b1-42c3-4b5c-ae76-2ebd6e0aa84f', 'b2f3f7e1-39a0-4d1c-8f6b-78575e1b6491', '2023-01-15', '2023-07-15', false, '2023-09-29 15:45:00'),
  ('c3d4d3c7-2e54-42af-8b65-9f00f692a17b', '69e044fb-2a27-420d-8f3f-2cec3c455639', 'd261e9b1-42c3-4b5c-ae76-2ebd6e0aa84f', 'c3d4d3c7-2e54-42af-8b65-9f00f692a17b', '2023-04-01', '2023-11-30', false, '2023-09-29 16:00:00');


INSERT INTO type_experience (id, name, created_at) VALUES
  ('cd6cfd9d-0f53-4e1e-9d8e-349c4e5d2b09', 'Estágio', '2023-09-29 14:30:00'),
  ('d261e9b1-42c3-4b5c-ae76-2ebd6e0aa84f', 'Profissional', '2023-09-29 14:45:00'),
  ('e2a5a5a7-5e9d-4e62-9d21-90689c87398a', 'Projeto', '2023-09-29 15:00:00'),
  ('f16e14c1-4e0d-464a-8a61-9565856829ec', 'Monitoria', '2023-09-29 15:15:00');


-- Inserir registros completos na tabela profile_experience
INSERT INTO profile_experience (id, profile_id, type_experience_id, local, description, start_date, end_date, present_date, created_at) VALUES
  ('cd6cfd9d-0f53-4e1e-9d8e-349c4e5d2b09', '69e044fb-2a27-420d-8f3f-2cec3c455639', 'cd6cfd9d-0f53-4e1e-9d8e-349c4e5d2b09', 'Empresa XYZ', 'Trabalhei como Analista de Marketing na Empresa XYZ, onde fui responsável por desenvolver campanhas de marketing digital, analisar métricas de desempenho de marketing e coordenar eventos promocionais. Colaborei com a equipe de vendas para aumentar a conscientização da marca e o envolvimento do cliente.', '2023-01-01', '2023-12-31', false, '2023-09-29 14:30:00'),
  ('d261e9b1-42c3-4b5c-ae76-2ebd6e0aa84f', '69e044fb-2a27-420d-8f3f-2cec3c455639', 'd261e9b1-42c3-4b5c-ae76-2ebd6e0aa84f', 'Hospital Geral ', 'Fui Enfermeira no Hospital Geral por três anos, atendendo pacientes em diversas especialidades médicas. Além de cuidar dos pacientes, também auxiliei na orientação de estagiários e na implementação de protocolos de segurança.', '2022-09-01', '2023-06-30', false, '2023-09-29 14:45:00'),
  ('e2a5a5a7-5e9d-4e62-9d21-90689c87398a', '69e044fb-2a27-420d-8f3f-2cec3c455639', 'e2a5a5a7-5e9d-4e62-9d21-90689c87398a', 'Estúdio de Design Criativo', 'Trabalhei como Designer Gráfico em um estúdio de design criativo, onde criei materiais gráficos para clientes de diversos setores. Colaborei com a equipe criativa para desenvolver identidades visuais e campanhas publicitárias.', '2023-03-15', '2023-09-15', true, '2023-09-29 15:00:00'),
  ('f16e14c1-4e0d-464a-8a61-9565856829ec', '05253510-5b6e-4342-b9e3-bccc62759645', 'f16e14c1-4e0d-464a-8a61-9565856829ec', 'Empresa ABC', 'Atuei como Desenvolvedor de Software na Empresa ABC, trabalhando no desenvolvimento de aplicativos móveis para dispositivos iOS. Participei do ciclo completo de desenvolvimento de software, desde a concepção até o lançamento do produto.', '2023-02-01', null, true, '2023-09-29 15:15:00'),
  ('a1e7e876-9d53-4ce9-b9d0-d399edf543ee', '05253510-5b6e-4342-b9e3-bccc62759645', 'cd6cfd9d-0f53-4e1e-9d8e-349c4e5d2b09', 'Escola Primária Local', 'Lecionei como Professor de Matemática na Escola Primária Local, onde ministrei aulas para alunos do 5º e 6º anos. Desenvolvi materiais didáticos, planejei atividades de ensino e acompanhei o progresso dos alunos.', '2023-08-01', '2023-12-31', false, '2023-09-29 15:30:00'),
  ('b2f3f7e1-39a0-4d1c-8f6b-78575e1b6491', '33d16b6f-6b68-4ede-bb6b-7d3e2789804c', 'd261e9b1-42c3-4b5c-ae76-2ebd6e0aa84f', 'Restaurante "Sabores Deliciosos"', 'Trabalhei como Atendente no restaurante "Sabores Deliciosos", onde atendi os clientes, anotei pedidos, servi refeições e assegurei um ambiente acolhedor. Também ajudei na organização de eventos especiais e no treinamento de novos funcionários.', '2023-01-15', '2023-07-15', false, '2023-09-29 15:45:00'),
  ('c3d4d3c7-2e54-42af-8b65-9f00f692a17b', '33d16b6f-6b68-4ede-bb6b-7d3e2789804c', 'cd6cfd9d-0f53-4e1e-9d8e-349c4e5d2b09', 'Empresa de Produção Audiovisual ', 'Fui Assistente de Produção em uma empresa de produção audiovisual, onde auxiliei na coordenação de filmagens, agendamento de elenco e locações, além de lidar com requisitos logísticos para projetos de vídeo e cinema.', '2023-04-01', '2023-11-30', false, '2023-09-29 16:00:00');
