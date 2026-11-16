-- Apply to existing databases before deploying the checkbox-only model.
ALTER TABLE tb_habito DROP COLUMN IF EXISTS meta;
ALTER TABLE tb_habito DROP COLUMN IF EXISTS unidade;
ALTER TABLE tb_habito RENAME COLUMN icone TO icon;
ALTER TABLE tb_habito RENAME COLUMN cor TO color;
ALTER TABLE tb_habito ADD COLUMN IF NOT EXISTS data_criacao DATE;
UPDATE tb_habito SET data_criacao = CURRENT_DATE WHERE data_criacao IS NULL;
ALTER TABLE tb_habito ALTER COLUMN data_criacao SET NOT NULL;

ALTER TABLE tb_historico DROP COLUMN IF EXISTS meta;
ALTER TABLE tb_historico DROP COLUMN IF EXISTS valor_atual;
ALTER TABLE tb_historico RENAME COLUMN status TO concluido;
