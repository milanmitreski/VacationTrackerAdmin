-- TABLE CREATION (COLUMNS, PRIMARY KEYS, FOREIGN KEYS, CONSTRAINTS)

DROP TABLE IF EXISTS used_vacation;

DROP TABLE IF EXISTS vacation;

DROP TABLE IF EXISTS employee;

CREATE TABLE employee (
                          email           VARCHAR(255) NOT NULL,
                          password        VARCHAR(255) NOT NULL DEFAULT '',
                          CONSTRAINT pk_employee PRIMARY KEY (email)
);

CREATE TABLE vacation (
                          email           VARCHAR(255) NOT NULL,
                          year            INTEGER NOT NULL,
                          vacation_days   INTEGER NOT NULL,
                          days_used       INTEGER NOT NULL DEFAULT 0,
                          CONSTRAINT pk_vacation PRIMARY KEY (email, year),
                          CONSTRAINT fk_employee FOREIGN KEY (email) REFERENCES employee (email) ON DELETE CASCADE ON UPDATE CASCADE,
                          CONSTRAINT chk_days_used_leq_vacation_days CHECK (days_used <= vacation.vacation_days)
);

CREATE TABLE used_vacation (
                               email           VARCHAR(255) NOT NULL,
                               year            INTEGER NOT NULL,
                               begin_date      DATE NOT NULL,
                               end_date        DATE NOT NULL,
                               CONSTRAINT pk_used_vacation PRIMARY KEY (email, year, begin_date),
                               CONSTRAINT fk_vacation FOREIGN KEY (email, year) REFERENCES vacation (email, year),
                               CONSTRAINT chk_begin_date_leq_end_date CHECK (begin_date <= end_date),
                               CONSTRAINT chk_year CHECK (year = DATE_PART('YEAR', begin_date) AND year = DATE_PART('YEAR', end_date))
    );

-- STORED PROCEDURES (TRIGGERS)

CREATE OR REPLACE FUNCTION count_business_days(from_date DATE, to_date DATE)
RETURNS BIGINT
AS $$
SELECT COUNT(d::DATE) AS d
FROM GENERATE_SERIES(from_date, to_date, '1 day'::INTERVAL) d
WHERE EXTRACT('dow' FROM d) NOT IN (0, 6)
    $$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION days_used_insert()
RETURNS TRIGGER
AS '
BEGIN
UPDATE vacation
SET days_used = days_used + count_business_days(NEW.begin_date, NEW.end_date)
WHERE email = NEW.email and year = NEW.year;
RETURN NEW;
END;
' LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION days_used_update()
RETURNS TRIGGER
AS '
BEGIN
UPDATE vacation
SET days_used = days_used + count_business_days(NEW.begin_date, NEW.end_date) - count_business_days(OLD.begin_date, OLD.end_date)
WHERE email = NEW.email AND year = NEW.year;
RETURN NEW;
END;
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION days_used_delete()
RETURNS TRIGGER
AS '
BEGIN
UPDATE vacation
SET days_used = days_used - count_business_days(OLD.begin_date, OLD.end_date)
WHERE email = OLD.email AND year = OLD.year;
RETURN old;
END;
' LANGUAGE PLPGSQL;

CREATE OR REPLACE TRIGGER trigger_days_used_insert
    AFTER INSERT
    ON used_vacation
    FOR EACH ROW
EXECUTE FUNCTION days_used_insert();

CREATE OR REPLACE TRIGGER trigger_days_used_update
    AFTER UPDATE
                                                 ON used_vacation
                                                 FOR EACH ROW
                                                 EXECUTE FUNCTION days_used_update();

CREATE OR REPLACE TRIGGER trigger_days_used_delete
    AFTER DELETE
ON used_vacation
    FOR EACH ROW
EXECUTE FUNCTION days_used_delete();