# Análise de Desempenho de Algoritmos de Ordenação em Ambientes Concorrentes e Paralelos

**Disciplina:** Computação Paralela  
**Instituição:** Universidade de Fortaleza — UNIFOR  
**Aluno:** Marcus Vinicius Herbster Ferreira  
**Período:** 2025.1  

---

## Resumo

Este trabalho apresenta uma análise comparativa do desempenho de quatro algoritmos de ordenação clássicos — Bubble Sort, Quick Sort, Merge Sort e Selection Sort — em suas versões sequenciais e paralelas, implementados na linguagem Java. O objetivo central é investigar como a paralelização impacta o tempo de execução de cada algoritmo sob diferentes tamanhos de entrada e diferentes quantidades de threads. Os resultados são registrados em arquivos CSV e analisados estatisticamente, permitindo identificar padrões de desempenho e determinar quais algoritmos se beneficiam mais do processamento paralelo.

---

## 1. Introdução

A busca por eficiência computacional é um tema central na ciência da computação moderna. Com o avanço dos processadores multicore, a programação paralela tornou-se uma abordagem essencial para extrair o máximo de desempenho das máquinas atuais. Nesse contexto, algoritmos de ordenação — amplamente utilizados em sistemas reais — representam um excelente objeto de estudo para comparar abordagens sequenciais e paralelas.

Este trabalho implementa e avalia quatro algoritmos de ordenação:

- **Bubble Sort:** algoritmo simples de complexidade O(n²), que compara pares adjacentes repetidamente. Paralelizado via Odd-Even Transposition Sort.
- **Quick Sort:** algoritmo de divisão e conquista com complexidade média O(n log n). Paralelizado com ForkJoinPool, dividindo as partições entre threads.
- **Merge Sort:** algoritmo estável de complexidade O(n log n), naturalmente paralelizável pela sua estrutura de divisão. Paralelizado com RecursiveAction via ForkJoinPool.
- **Selection Sort:** algoritmo O(n²) que seleciona o mínimo a cada iteração. Paralelizado pela busca simultânea do mínimo em segmentos do array via ExecutorService.

A abordagem adotada consiste em executar cada algoritmo em modo serial (1 thread) e em modo paralelo (2, 4 e 8 threads), variando o tamanho do array de entrada e o tipo de dado (aleatório, ordenado e invertido), coletando pelo menos 5 amostras de tempo por configuração.

---

## 2. Metodologia

### 2.1 Ambiente de Execução

- **Linguagem:** Java 17
- **Gerenciador de dependências:** Maven 3.x
- **APIs de concorrência utilizadas:** `ForkJoinPool`, `RecursiveAction`, `ExecutorService`, `Executors.newFixedThreadPool`
- **Sistema Operacional:** Windows 11
- **Hardware:** Processador multicore

### 2.2 Framework de Testes

Foi desenvolvido um framework próprio (`TestRunner`) responsável por:

- Gerar arrays de três naturezas distintas:
  - **Aleatório (`random`):** valores gerados com `Random` e semente fixa (42) para reprodutibilidade
  - **Ordenado (`sorted`):** array já em ordem crescente (melhor caso)
  - **Invertido (`reverse`):** array em ordem decrescente (pior caso para vários algoritmos)
- Copiar o array original antes de cada execução, garantindo que todos os algoritmos recebam os mesmos dados
- Medir o tempo de execução via `System.nanoTime()`, convertido para milissegundos

### 2.3 Configurações dos Experimentos

| Parâmetro          | Valores                          |
|--------------------|----------------------------------|
| Tamanhos de array  | 1.000 / 5.000 / 10.000 / 30.000 |
| Tipos de dados     | Aleatório, Ordenado, Invertido   |
| Threads (paralelo) | 2, 4, 8                          |
| Amostras por teste | 5                                |

### 2.4 Análise Estatística

Para cada configuração (algoritmo + tipo + tamanho + threads), foram coletadas 5 amostras de tempo. A análise considera:

- **Média:** tempo médio de execução das 5 amostras
- **Speedup:** razão entre o tempo serial e o tempo paralelo (`T_serial / T_paralelo`)
- **Comparação entre algoritmos:** verificação de qual abordagem é mais vantajosa por tamanho de entrada

---

## 3. Estrutura do Projeto

```
sorting-analysis/
├── pom.xml
├── results/
│   └── resultados.csv
└── src/
    └── main/
        └── java/
            └── com/
                └── sorting/
                    ├── Main.java
                    ├── algorithms/
                    │   ├── BubbleSort.java
                    │   ├── QuickSort.java
                    │   ├── MergeSort.java
                    │   └── SelectionSort.java
                    ├── parallel/
                    │   ├── ParallelBubbleSort.java
                    │   ├── ParallelQuickSort.java
                    │   ├── ParallelMergeSort.java
                    │   └── ParallelSelectionSort.java
                    ├── framework/
                    │   └── TestRunner.java
                    └── results/
                        └── CsvWriter.java
```

---

## 4. Resultados e Discussão

### 4.1 Comportamento Serial

Os algoritmos seriais apresentaram o comportamento esperado pela teoria:

- **Bubble Sort e Selection Sort** — complexidade O(n²) — mostraram crescimento quadrático do tempo conforme o aumento do tamanho do array. Para arrays de 30.000 elementos, os tempos foram dezenas de vezes maiores que os de Quick Sort e Merge Sort.
- **Quick Sort e Merge Sort** — complexidade O(n log n) — mantiveram tempos muito menores mesmo para os maiores arrays testados.
- Arrays **já ordenados** beneficiaram algoritmos como Bubble Sort mas representaram o pior caso para Quick Sort sem pivot aleatório.
- Arrays **invertidos** representaram o pior caso para Selection Sort e Bubble Sort.

### 4.2 Impacto da Paralelização

A paralelização trouxe ganhos significativos para os algoritmos de divisão e conquista:

- **Parallel Merge Sort** apresentou o maior speedup com o aumento de threads, graças à sua estrutura naturalmente recursiva e independente entre as metades.
- **Parallel Quick Sort** também obteve ganhos expressivos, especialmente para arrays grandes e aleatórios.
- **Parallel Bubble Sort** (Odd-Even Transposition) apresentou ganhos modestos — o overhead de sincronização entre fases limitou o speedup.
- **Parallel Selection Sort** obteve algum ganho ao buscar o mínimo em paralelo, mas o loop externo serial limitou a escalabilidade.

### 4.3 Escalabilidade com Threads

| Algoritmo               | 2 threads | 4 threads | 8 threads |
|-------------------------|-----------|-----------|-----------|
| Parallel Merge Sort     | ~1.8x     | ~3.2x     | ~4.5x     |
| Parallel Quick Sort     | ~1.6x     | ~2.8x     | ~3.8x     |
| Parallel Bubble Sort    | ~1.1x     | ~1.3x     | ~1.4x     |
| Parallel Selection Sort | ~1.2x     | ~1.5x     | ~1.7x     |

> Valores de speedup aproximados. Consulte o arquivo `results/resultados.csv` para os dados completos.

### 4.4 Observações Gerais

- O ganho de paralelização diminui após 4 threads para arrays menores (1.000–5.000 elementos), pois o overhead de criação e sincronização de threads supera o benefício.
- Para arrays grandes (30.000+ elementos), o Parallel Merge Sort com 8 threads foi consistentemente o mais rápido.
- Algoritmos O(n²) não se beneficiam tanto da paralelização quanto os O(n log n), pois a quantidade de trabalho total ainda é muito maior.
- A natureza dos dados (aleatório, ordenado, invertido) impacta diretamente o desempenho, especialmente do Quick Sort.

---

## 5. Conclusão

Este trabalho demonstrou na prática os conceitos fundamentais de computação paralela aplicados a algoritmos de ordenação. Os resultados confirmam que:

1. Algoritmos de divisão e conquista (Merge Sort, Quick Sort) são naturalmente mais paralelizáveis e obtêm speedups expressivos com múltiplas threads.
2. Algoritmos simples como Bubble Sort e Selection Sort, apesar de paralelizáveis, têm ganhos limitados devido ao overhead de sincronização e à natureza sequencial de suas operações.
3. Existe um ponto de saturação no número de threads — aumentar indefinidamente as threads não garante ganho proporcional de desempenho.
4. O tipo de dado de entrada (aleatório, ordenado, invertido) impacta significativamente o desempenho, especialmente dos algoritmos sem adaptação ao estado do array.

A experiência reforça a importância de escolher o algoritmo adequado ao problema e ao ambiente de execução, considerando não apenas a complexidade teórica, mas também as características do hardware disponível.

---

## 6. Referências

- CORMEN, T. H. et al. **Introdução a Algoritmos**. 3. ed. MIT Press, 2009.
- GOETZ, B. et al. **Java Concurrency in Practice**. Addison-Wesley, 2006.
- Oracle. **ForkJoinPool (Java SE 17)**. Disponível em: https://docs.oracle.com/en/java/api/java.base/java/util/concurrent/ForkJoinPool.html
- Oracle. **ExecutorService (Java SE 17)**. Disponível em: https://docs.oracle.com/en/java/api/java.base/java/util/concurrent/ExecutorService.html
- SEDGEWICK, R.; WAYNE, K. **Algorithms**. 4. ed. Addison-Wesley, 2011.

---

## 7. Anexos — Como Executar

```bash
# 1. Clone o repositório
git clone https://github.com/marcus833/sorting-analysis.git
cd sorting-analysis

# 2. Compile o projeto
mvn compile

# 3. Execute os testes (gera o CSV automaticamente)
mvn exec:java

# 4. O arquivo de resultados estará em:
# results/resultados.csv
```

Todos os códigos-fonte estão disponíveis no repositório GitHub abaixo.

---

## Link do Repositório

**https://github.com/marcus833/sorting-analysis**

---

*Trabalho desenvolvido para a disciplina de Computação Paralela — UNIFOR, 2025.1*