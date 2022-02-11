drop table if exists survey;
create table survey(
                       id serial primary key,
                       survey_Name varchar(50),
                       start_Date varchar(50),
                       end_Date varchar(50),
                       is_active varchar(50)
);

drop table if exists question;
create table question(
                         id serial primary key ,
                         question varchar(100)
);
drop table if exists surveyslike;
create table surveysLike(
                            id serial ,
                            survey_id int not null ,
                            question_id int not null ,
                            primary key (survey_id,question_id),
                            foreign key (survey_id) references survey(id) on delete cascade ,
                            foreign key (question_id) references question(id) on  delete cascade
);
insert into question(question)
values
('Что такое СУБД?'),
('Какие типы СУБД в соответствии с моделями данных вы знаете?'),
('Что такое первичный ключ?'),
('Когда используется PRIMARY KEY?'),
('А что такое внешний ключ?'),
('Какие ещё ограничения вы знаете, как они работают и указываются?'),
('Для чего используется ключевое слово ORDER BY?'),
('Назовите четыре основных типа соединения в SQL'),
('А что такое Self JOIN?'),
('Для чего нужен оператор UNION?'),
('Как работают подстановочные знаки?'),
('Какими бывают подстановочные знаки?'),
('Что делают псевдонимы Aliases?'),
('Для чего нужен оператор INSERT INTO SELECT?'),
('Что такое нормализация и денормализация?'),
('Объясните разницу между командами DELETE и TRUNCATE'),
('Чем VARCHAR отличается от NVARCHAR?'),
('Как выбрать записи с нечётными Id?'),
('Как найти дубли в поле email?'),
('При выборке из таблицы прибавьте к дате 1 день'),
('Выберите только уникальные имена'),
('Найдите в таблице среднюю зарплату работников'),
('А теперь получите список сотрудников с зарплатой выше средней'),
('Переименуйте таблицу');
