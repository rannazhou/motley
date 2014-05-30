CREATE TABLE definitions
SELECT uw_translated_content.translated_content_id, uw_text.text_text
	from uw_translated_content, uw_text
	where uw_translated_content.language_id = 85
		and uw_translated_content.text_id = uw_text.text_id;

CREATE TABLE meanings
SELECT uw_defined_meaning.defined_meaning_id, uw_translated_content.translated_content_id
	from uw_defined_meaning, uw_translated_content
	where uw_defined_meaning.meaning_text_tcid = uw_translated_content.translated_content_id;

--why won't this one create?
CREATE TABLE meaning_with_def
SELECT meanings.defined_meaning_id, definitions.text_text
	from meanings, definitions
	where meanings.translated_content_id = definitions.translated_content_id;



--gets all english syntrans
CREATE TABLE expression_eng 
SELECT uw_syntrans.defined_meaning_id, uw_expression.spelling
	from uw_syntrans, uw_expression
	where uw_expression.language_id = 85 and uw_expression.remove_transaction_id IS NULL
		and uw_syntrans.expression_id = uw_expression.expression_id;

--gets all french syntrans
CREATE TABLE expression_fr
SELECT uw_syntrans.defined_meaning_id, uw_expression.spelling 
	from uw_syntrans, uw_expression
	where uw_expression.language_id = 86 and uw_expression.remove_transaction_id IS NULL
		and uw_syntrans.expression_id = uw_expression.expression_id;

CREATE TABLE translations
	SELECT expression_eng.defined_meaning_id, expression_eng.spelling eng,  expression_fr.spelling fr
	from expression_eng, expression_fr
	where expression_eng.defined_meaning_id = expression_fr.defined_meaning_id;

CREATE TABLE dictionary
	SELECT translations.eng, translations.fr, meaning_with_def.text_text def
	from translations, meaning_with_def
	where translations.defined_meaning_id = meaning_with_def.defined_meaning_id;

-- make things play nice once in csv format
UPDATE translations
   SET fr = REPLACE(fr,'"','\'');
UPDATE translations
   SET eng = REPLACE(eng,'"','\'');

--ouput to csv
SELECT defined_meaning_id, eng, fr 
INTO OUTFILE 'translations.csv'
CHARACTER SET utf8 
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
ESCAPED BY '\\'
LINES TERMINATED BY '\n'
FROM translations WHERE 1;