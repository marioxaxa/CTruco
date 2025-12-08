# Guia de Uso do OpenJML

Este guia explica como usar o script `run_openjml.sh` para verificar especificações JML (Java Modeling Language) no projeto CTruco.

## Pré-requisitos

### 1. Instalação do OpenJML

Primeiro, você precisa ter o OpenJML instalado. Existem duas opções:

**Opção A: Instalação padrão (recomendado)**
```bash
# Criar diretório no home
mkdir -p ~/openjml

# Baixar o OpenJML
cd ~/openjml
wget https://github.com/OpenJML/OpenJML/releases/latest/download/openjml

# Dar permissão de execução
chmod +x openjml

# Definir variável de ambiente (adicione ao ~/.bashrc para tornar permanente)
export OPENJML_HOME=~/openjml
```

**Opção B: Arquivo JAR local**
```bash
# Colocar openjml.jar na raiz do projeto CTruco
cp /caminho/para/openjml.jar ./
```

### 2. Gerar o Classpath

Antes de executar o OpenJML, é necessário gerar o arquivo de classpath:

```bash
# Compilar o projeto e gerar o classpath
mvn clean compile dependency:build-classpath -Dmdep.outputFile=target/openjml-classpath.txt
```

## Uso Básico

### Sintaxe Geral

```bash
./run_openjml.sh [opções] [modo-verificação] [caminho-alvo]
```

### Modos de Verificação

- **`-check`** - Verificação básica de sintaxe JML (padrão)
- **`-esc`** - Extended Static Checking (verificação estática estendida)
- **`-rac`** - Runtime Assertion Checking (gera código com verificação em tempo de execução)

## Exemplos de Uso

### 1. Verificar todo o projeto com ESC (com filtro)

```bash
./run_openjml.sh -esc -filter .
```

Este comando:
- Verifica **todos** os arquivos Java com especificações JML
- Usa o modo **ESC** (verificação estática estendida)
- **Filtra** a saída para mostrar apenas erros/warnings relevantes de JML
- Ignora erros de dependências externas (Gson, Guava, etc.)

### 2. Verificar apenas o módulo `domain`

```bash
./run_openjml.sh -esc -filter domain
```

Verifica apenas os arquivos dentro do módulo `domain`.

### 3. Verificar o módulo `bot-spi`

```bash
./run_openjml.sh -esc -filter bot-spi
```

### 4. Verificação básica sem filtro

```bash
./run_openjml.sh -check domain
```

Mostra **toda** a saída, incluindo erros de compilação e dependências.

### 5. Usar caminho customizado para OpenJML

```bash
./run_openjml.sh -j /caminho/para/openjml.jar -esc -filter .
```

### 6. Ver estatísticas de anotações JML

```bash
./run_openjml.sh -stats
```

Mostra quantos arquivos têm anotações JML vs. total de arquivos.

## Opções Disponíveis

| Opção | Descrição |
|-------|-----------|
| `-j <caminho>` | Especifica o caminho customizado para o OpenJML |
| `-filter` | Filtra a saída para mostrar apenas warnings/erros de JML |
| `-stats` | Exibe estatísticas de arquivos com anotações JML |
| `-check` | Modo de verificação básica (padrão) |
| `-esc` | Modo de verificação estática estendida |
| `-rac` | Modo de geração de código com verificação em runtime |

## O que o Filtro Faz?

Quando você usa a opção `-filter`, o script:

✅ **Mostra:**
- Erros e warnings relacionados a JML
- Violações de `requires`, `ensures`, `invariant`
- Problemas com `assertion`, `postcondition`, `precondition`

❌ **Oculta:**
- Erros de `package not visible` (ex: dependências externas)
- Erros de `static import only from classes`
- Erros de `cannot find symbol`
- Erros de `package does not exist`
- Warnings em `module-info.java`

## Estrutura de Arquivos Verificados

O script verifica apenas:
- ✅ Arquivos em `src/main/java/`
- ✅ Arquivos `.java` (exceto `module-info.java`)
- ❌ **NÃO** verifica arquivos em `test/`
- ❌ **NÃO** verifica arquivos em `.volume/`

## Workflow Recomendado

### 1. Desenvolvimento Inicial

```bash
# Ver estatísticas
./run_openjml.sh -stats

# Verificação rápida de um módulo
./run_openjml.sh -check -filter domain
```

### 2. Verificação Completa

```bash
# Gerar classpath atualizado
mvn clean compile dependency:build-classpath -Dmdep.outputFile=target/openjml-classpath.txt

# Verificação ESC completa
./run_openjml.sh -esc -filter .
```

### 3. Verificação de Módulo Específico

```bash
# Verificar apenas bot-spi
./run_openjml.sh -esc -filter bot-spi

# Verificar apenas domain
./run_openjml.sh -esc -filter domain

# Verificar apenas console
./run_openjml.sh -esc -filter console
```

## Solução de Problemas

### Erro: "openjml.jar not found"

**Solução:**
```bash
# Verificar se OPENJML_HOME está definido
echo $OPENJML_HOME

# Se não estiver, definir:
export OPENJML_HOME=~/openjml

# Ou usar a opção -j:
./run_openjml.sh -j ~/openjml/openjml -esc -filter .
```

### Erro: "target/openjml-classpath.txt not found"

**Solução:**
```bash
# Gerar o classpath
mvn clean compile dependency:build-classpath -Dmdep.outputFile=target/openjml-classpath.txt
```

### Muitos erros de "package not visible"

**Solução:**
Use a opção `-filter` para ocultar esses erros:
```bash
./run_openjml.sh -esc -filter .
```

### Nenhum arquivo encontrado

**Problema:** O caminho especificado não contém arquivos Java.

**Solução:**
```bash
# Verificar se está na raiz do projeto
pwd

# Listar módulos disponíveis
ls -d */

# Usar o caminho correto
./run_openjml.sh -esc -filter domain
```

## Interpretando os Resultados

### Saída Típica (sem erros)

```
Using OpenJML: /home/allyson/openjml/openjml
Finding source files in '.'...
Found 250 source files.
Running OpenJML with args:  -esc
Output filtered for JML warnings/errors...
Done.
```

### Saída com Erros JML

```
./domain/src/main/java/com/bueno/domain/usecases/game/usecase/PlayCardUseCase.java:45: warning: The prover cannot establish an assertion (Postcondition) in method doPlay
./domain/src/main/java/com/bueno/domain/entities/Game.java:120: error: invariant might not hold on exit from constructor
Done.
```

## Dicas e Boas Práticas

1. **Use sempre `-filter`** para verificações de JML (ignora ruído de dependências externas)

2. **Compile antes de verificar** para garantir que o classpath está atualizado:
   ```bash
   mvn clean compile && ./run_openjml.sh -esc -filter .
   ```

3. **Verifique módulo por módulo** durante o desenvolvimento:
   ```bash
   ./run_openjml.sh -esc -filter domain
   ```

4. **Use `-check` para verificações rápidas**, `-esc` para verificação completa:
   ```bash
   # Rápido
   ./run_openjml.sh -check -filter domain
   
   # Completo (mais lento)
   ./run_openjml.sh -esc -filter domain
   ```

5. **Monitore as estatísticas** periodicamente:
   ```bash
   ./run_openjml.sh -stats
   ```

## Integração com CI/CD

Para integrar com pipelines de CI/CD:

```bash
#!/bin/bash
# Script de CI para verificação JML

# 1. Compilar e gerar classpath
mvn clean compile dependency:build-classpath -Dmdep.outputFile=target/openjml-classpath.txt

# 2. Executar verificação
./run_openjml.sh -esc -filter .

# 3. Verificar código de saída
if [ $? -ne 0 ]; then
    echo "❌ Verificação JML falhou!"
    exit 1
else
    echo "✅ Verificação JML passou!"
fi
```

## Referências

- [OpenJML Documentation](http://www.openjml.org/)
- [JML Reference Manual](http://www.eecs.ucf.edu/~leavens/JML//refman/jmlrefman.html)
- [CTruco Project Repository](https://github.com/lucas-ifsp/CTruco)

## Suporte

Para problemas ou dúvidas:
1. Verifique a seção "Solução de Problemas" acima
2. Consulte a documentação oficial do OpenJML
3. Abra uma issue no repositório do projeto

