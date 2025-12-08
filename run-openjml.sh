#!/bin/bash
# Script para rodar OpenJML no projeto CTruco
# Usage: ./run-openjml.sh [check|rac|esc] [module] [file]
#        ./run-openjml.sh check-entities  (verifica apenas entidades sem dependências complexas)

# Configurações
MODE="${1:-check}"
MODULE="${2:-domain}"
FILE="${3:-}"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;37m'
NC='\033[0m' # No Color

# Verifica se o OpenJML está disponível
if ! command -v openjml &> /dev/null; then
    echo -e "${RED}ERRO: OpenJML não encontrado no PATH${NC}"
    echo -e "${YELLOW}Por favor, instale o OpenJML de https://www.openjml.org/${NC}"
    exit 1
fi

echo -e "${GREEN}✓ OpenJML encontrado${NC}"

# Modo especial: verificar apenas entidades (sem problemas de generics)
if [ "$MODE" = "check-entities" ]; then
    echo -e "${CYAN}Verificando apenas entidades do domínio (sem dependências complexas)...${NC}"

    TEMP_DIR=$(mktemp -d)
    trap "rm -rf $TEMP_DIR" EXIT

    # Copia arquivos fonte sem module-info
    for dir in domain/src/main/java bot-spi/src/main/java; do
        if [ -d "$dir" ]; then
            find "$dir" -name "*.java" -not -name "module-info.java" -exec cp --parents {} "$TEMP_DIR" \;
        fi
    done

    SOURCEPATH="$TEMP_DIR/domain/src/main/java:$TEMP_DIR/bot-spi/src/main/java"

    # Lista de arquivos a verificar (apenas arquivos sem problemas de generics do OpenJML)
    FILES=(
        # bot-spi - todos funcionam!
        "$TEMP_DIR/bot-spi/src/main/java/com/bueno/spi/model/TrucoCard.java"
        "$TEMP_DIR/bot-spi/src/main/java/com/bueno/spi/model/CardRank.java"
        "$TEMP_DIR/bot-spi/src/main/java/com/bueno/spi/model/CardSuit.java"
        "$TEMP_DIR/bot-spi/src/main/java/com/bueno/spi/model/CardToPlay.java"
        "$TEMP_DIR/bot-spi/src/main/java/com/bueno/spi/model/GameIntel.java"
        "$TEMP_DIR/bot-spi/src/main/java/com/bueno/spi/service/BotServiceProvider.java"
        # domain - apenas os que não usam EnumSet/Stream com generics complexos
        "$TEMP_DIR/domain/src/main/java/com/bueno/domain/entities/deck/Card.java"
        "$TEMP_DIR/domain/src/main/java/com/bueno/domain/entities/deck/Deck.java"
        "$TEMP_DIR/domain/src/main/java/com/bueno/domain/entities/deck/Rank.java"
        "$TEMP_DIR/domain/src/main/java/com/bueno/domain/entities/deck/Suit.java"
        "$TEMP_DIR/domain/src/main/java/com/bueno/domain/entities/player/Player.java"
    )

    # Filtra apenas arquivos que existem
    EXISTING_FILES=()
    for f in "${FILES[@]}"; do
        if [ -f "$f" ]; then
            EXISTING_FILES+=("$f")
        fi
    done

    echo -e "${CYAN}Processando ${#EXISTING_FILES[@]} arquivos...${NC}"
    echo ""

    openjml -sourcepath "$SOURCEPATH" -check "${EXISTING_FILES[@]}"
    EXIT_CODE=$?

    echo ""
    if [ $EXIT_CODE -eq 0 ]; then
        echo -e "${GREEN}✓ OpenJML executado com sucesso!${NC}"
    else
        echo -e "${YELLOW}⚠ OpenJML encontrou problemas (exit code: $EXIT_CODE)${NC}"
    fi

    exit $EXIT_CODE
fi

# Define o diretório do módulo
MODULE_PATH="$MODULE"

if [ ! -d "$MODULE_PATH" ]; then
    echo -e "${RED}ERRO: Módulo '$MODULE' não encontrado${NC}"
    exit 1
fi

# Define o caminho dos arquivos fonte
SOURCE_PATH="$MODULE_PATH/src/main/java"

if [ ! -d "$SOURCE_PATH" ]; then
    echo -e "${RED}ERRO: Diretório de fontes não encontrado: $SOURCE_PATH${NC}"
    exit 1
fi

# Verifica se precisa compilar os módulos
if [ ! -d "bot-spi/target/classes" ]; then
    echo -e "${YELLOW}Compilando o projeto...${NC}"
    mvn clean compile -DskipTests -q
fi

# Monta o classpath manualmente usando os diretórios target/classes e as dependências do Maven
echo -e "${CYAN}Preparando classpath...${NC}"

MAVEN_CLASSPATH=""

# Adiciona os diretórios de classes compiladas dos módulos
for mod in bot-spi domain bot-impl console persistence; do
    if [ -d "$mod/target/classes" ]; then
        if [ -n "$MAVEN_CLASSPATH" ]; then
            MAVEN_CLASSPATH="$MAVEN_CLASSPATH:$mod/target/classes"
        else
            MAVEN_CLASSPATH="$mod/target/classes"
        fi
    fi
done

# Adiciona dependências do diretório .m2 (Spring e outras)
M2_REPO="$HOME/.m2/repository"
if [ -d "$M2_REPO" ]; then
    # Spring core
    SPRING_JARS=$(find "$M2_REPO/org/springframework" -name "*.jar" 2>/dev/null | tr '\n' ':')
    MAVEN_CLASSPATH="$MAVEN_CLASSPATH:$SPRING_JARS"
fi

# Cria diretório temporário para arquivos fonte sem module-info
TEMP_DIR=$(mktemp -d)
trap "rm -rf $TEMP_DIR" EXIT

# Copia todos os arquivos .java exceto module-info.java para o diretório temporário
echo -e "${CYAN}Preparando arquivos fonte...${NC}"
for dir in domain/src/main/java bot-spi/src/main/java bot-impl/src/main/java console/src/main/java persistence/src/main/java; do
    if [ -d "$dir" ]; then
        find "$dir" -name "*.java" -not -name "module-info.java" -exec cp --parents {} "$TEMP_DIR" \;
    fi
done

# Prepara o sourcepath usando os diretórios temporários
SOURCEPATH="$TEMP_DIR/domain/src/main/java:$TEMP_DIR/bot-spi/src/main/java:$TEMP_DIR/bot-impl/src/main/java:$TEMP_DIR/console/src/main/java:$TEMP_DIR/persistence/src/main/java"

# Prepara os argumentos do comando (usa -cp ao invés de --classpath)
OPENJML_ARGS="-sourcepath $SOURCEPATH"

# Adiciona classpath se disponível (OpenJML usa -cp)
if [ -n "$MAVEN_CLASSPATH" ]; then
    OPENJML_ARGS="$OPENJML_ARGS -cp $MAVEN_CLASSPATH"
    echo -e "${GREEN}✓ Classpath configurado${NC}"
fi

case "$MODE" in
    check)
        echo -e "${CYAN}Executando OpenJML Type Checking...${NC}"
        OPENJML_ARGS="$OPENJML_ARGS -check"
        ;;
    rac)
        echo -e "${CYAN}Executando OpenJML Runtime Assertion Checking...${NC}"
        RAC_OUTPUT="target/rac-classes"
        mkdir -p "$RAC_OUTPUT"
        OPENJML_ARGS="$OPENJML_ARGS -rac -d $RAC_OUTPUT"
        ;;
    esc)
        echo -e "${CYAN}Executando OpenJML Extended Static Checking...${NC}"
        OPENJML_ARGS="$OPENJML_ARGS -esc -prover z3_4_7"
        ;;
    *)
        echo -e "${RED}ERRO: Modo inválido '$MODE'. Use: check, rac, esc ou check-entities${NC}"
        exit 1
        ;;
esac

# Verifica se é arquivo específico ou todos os arquivos
if [ -n "$FILE" ]; then
    # Arquivo específico
    TARGET_PATH="$TEMP_DIR/$SOURCE_PATH/$FILE"
    if [ ! -f "$TARGET_PATH" ]; then
        echo -e "${RED}ERRO: Arquivo não encontrado: $TARGET_PATH${NC}"
        exit 1
    fi

    echo -e "${GRAY}Executando: openjml ... $FILE${NC}"
    echo ""
    openjml $OPENJML_ARGS "$TARGET_PATH"
    EXIT_CODE=$?
else
    # Todos os arquivos .java do módulo
    TEMP_SOURCE="$TEMP_DIR/$SOURCE_PATH"

    # Conta quantos arquivos serão processados
    FILE_COUNT=$(find "$TEMP_SOURCE" -name "*.java" 2>/dev/null | wc -l)
    echo -e "${CYAN}Processando $FILE_COUNT arquivos .java...${NC}"
    echo ""

    # Executa OpenJML em todos os arquivos
    find "$TEMP_SOURCE" -name "*.java" | xargs openjml $OPENJML_ARGS
    EXIT_CODE=$?
fi

echo ""
if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}✓ OpenJML executado com sucesso!${NC}"
else
    echo -e "${YELLOW}⚠ OpenJML encontrou problemas (exit code: $EXIT_CODE)${NC}"
fi

# Exemplos de uso
echo -e "\n${CYAN}=== EXEMPLOS DE USO ===${NC}"
echo -e "${GRAY}Type checking do módulo domain:${NC}"
echo -e "  ./run-openjml.sh check domain"
echo -e "${GRAY}Type checking do módulo bot-spi:${NC}"
echo -e "  ./run-openjml.sh check bot-spi"
echo -e "${GRAY}Verificar apenas entidades (sem erros de generics):${NC}"
echo -e "  ./run-openjml.sh check-entities"
echo -e "${GRAY}Runtime Assertion Checking:${NC}"
echo -e "  ./run-openjml.sh rac domain"
echo -e "${GRAY}Extended Static Checking:${NC}"
echo -e "  ./run-openjml.sh esc domain"
echo -e "${GRAY}Verificar arquivo específico:${NC}"
echo -e "  ./run-openjml.sh check domain com/bueno/domain/entities/deck/Card.java"

exit $EXIT_CODE

