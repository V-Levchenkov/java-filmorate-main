# java-filmorate
Template repository for Filmorate project.        
![Database schema](https://github.com/V-Levchenkov/java-filmorate-main/blob/master/src/main/resources/QuickDBD-export.png)        
Основная информация о таблицах.
users - информация о пользователях
friends - информация о друзьях пользователя. Поле status: true - если дружба между пользователями подтверждена, false - не подтверждена.
films - информация о фильмах
film_genres - содержит перечень всех жанров кино
mpa_age_ratings - содержит перечень возрастных рейтингов Ассоциации кинокомпаний (Motion Picture Association, сокращённо МРА)
film_genre_rel - соотнесение фильма с жанрами.
film_likes - перечень лайков, поставленных пользователями фильму.
Примеры основных SQL запросов для выгрузки данных о фильмах и пользователях.
Все пользователи:
SELECT *
FROM users;
Пользователь с определенным {id}:
SELECT *
FROM users
WHERE id = ?;
Все друзья пользователя с определенным {id}:
SELECT *
FROM users
WHERE id IN (SELECT friend_id
             FROM friends
             WHERE user_id = ?);
Список общих друзей пользователей с {id1} и {id2}:
SELECT *
FROM users
WHERE id IN (SELECT DISTINCT friend_id
             FROM friends
             WHERE user_id IN (?,?) --id1,id2
                  AND friend_id NOT IN (?,?) --id1,id2
            );
Все фильмы:
SELECT f.*, mar.rating_name
FROM films f
JOIN mpa_age_ratings mar
	ON f.mpa_rating_id = mar.rating_id;
Фильм с определенным {id}:
SELECT f.*, mar.rating_name
FROM films f
JOIN mpa_age_ratings mar
  ON f.mpa_rating_id = mar.rating_id
WHERE f.id = ?;
Список жанров фильма с определенным {id}:
SELECT fg.genre_name
FROM film_genre_rel fgr
JOIN film_genres fg
	ON fgr.genre_id = fg.genre_id
WHERE fgr.film_id = ?;
Список {N} самых популярных фильмов:
SELECT f.*, mar.rating_name
FROM films f
JOIN mpa_age_ratings mar
  ON f.mpa_rating_id = mar.rating_id
ORDER BY rate DESC
LIMIT ?;
