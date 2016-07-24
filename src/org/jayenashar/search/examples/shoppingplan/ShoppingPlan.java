package org.jayenashar.search.examples.shoppingplan;

import au.edu.unsw.cse.jayen.search.AStarSearch;
import au.edu.unsw.cse.jayen.search.Action;
import au.edu.unsw.cse.jayen.search.ActionStatePair;
import au.edu.unsw.cse.jayen.search.StateSpaceSearchProblem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * https://code.google.com/codejam/contest/32003/dashboard#s=p3
 * <p>
 * Created by jayen on 23/07/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class ShoppingPlan {
    /**
     * @param args one input filename
     */
    public static void main(String[] args) throws IOException {
        final String filename = args[0];
        final BufferedReader reader = new BufferedReader(new FileReader(filename));
        final byte numCases = Byte.parseByte(reader.readLine());
        final Case[] cases = new Case[numCases];
        final double[] minimumSpends = new double[numCases];
        IntStream.range(0, numCases).sequential().forEachOrdered(caseIndex -> {
            try {
                cases[caseIndex] = new Case(reader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        IntStream.range(0, numCases).parallel().forEach(caseIndex -> {
            minimumSpends[caseIndex] = cases[caseIndex].minimumSpend();
            System.out.println("Case #" + (caseIndex + 1) + ": " + String.format("%.7f", minimumSpends[caseIndex]));
        });
        System.out.println();
        IntStream.range(0, numCases).forEachOrdered(caseIndex -> System.out
                .println("Case #" + (caseIndex + 1) + ": " + String.format("%.7f", minimumSpends[caseIndex])));
    }

    private static class Case {
        private final short       priceOfGas;
        private final List<Item>  items;
        private final List<Store> stores;
        private final Store       home;
        private final byte        numStores;

        private Case(final BufferedReader reader) throws IOException {
            final String[] itemsStoresGas = reader.readLine().split(" ");
            final byte numItems = Byte.parseByte(itemsStoresGas[0]);
            numStores = Byte.parseByte(itemsStoresGas[1]);
            priceOfGas = Short.parseShort(itemsStoresGas[2]);

            final String[] itemsArray = reader.readLine().split(" ");
            items = new ArrayList<>(itemsArray.length);
            for (int i = 0; i < itemsArray.length; i++)
                items.add(new Item(itemsArray[i], i));
            assert numItems == items.size();

            stores = new ArrayList<>(numStores + 1);
            for (int storeIndex = 0; storeIndex < numStores; storeIndex++)
                stores.add(new Store(reader.readLine(), stores));
            home = new Store("0 0 ", stores);
            stores.add(home);
        }

        private double minimumSpend() {
            final AStarSearch<State> search = new AStarSearch<>(state -> {
                final int itemsMinPrice = state.itemsToBuy.stream().map(index -> items.get(index).getMinPrice()).sum();
                final double goNearestStoreAndHomePrice = stores.stream().filter(store -> !state.storesVisited.contains(store) &&
                                                                                          store != home
                                                                                 // somehow filtering out stores that have no items we
                                                                                 // still need to buy makes it explore MORE states
                /* && store.items.stream().anyMatch(storeItem -> state.itemsToBuy.get(storeItem.item.index))*/)
                                                                .mapToDouble(store -> state.store.goStorePrice(store) +
                                                                                      store.goStorePrice(home)).min()
                                                                .orElse(Double.POSITIVE_INFINITY);
                return itemsMinPrice + goNearestStoreAndHomePrice;
            });
            final double minimumSpend = search.search(new StateSpaceSearchProblem<State>() {
                @Override
                public Iterable<State> initialStates() {
                    final BitSet itemsToBuy = new BitSet(items.size());
                    itemsToBuy.set(0, items.size());
                    return Collections.singleton(new State(Collections.emptyList(),
                                                           new BitSet(items.size()),
                                                           itemsToBuy,
                                                           home,
                                                           0));
                }

                @Override
                public boolean isGoal(final State state) {
                    return state.itemsToBuy.isEmpty();
                }

                @Override
                public Iterable<ActionStatePair<State>> successor(final State state) {
                    return stores.stream().filter(store -> !state.storesVisited.contains(store) && store != home).map(store -> {
                        // calculate these once per store, even if i may not go home
                        final double priceOfGasToStore = state.store.goStorePrice(store);
                        final double priceOfGasToHome = store.goStorePrice(home);
                        final List<Store> storesVisited = new ArrayList<>(state.storesVisited.size() + 1);
                        storesVisited.addAll(state.storesVisited);
                        storesVisited.add(store);

                        final List<Store.Item> itemsMayBy = store.items.stream().filter(storeItem -> storeItem.isToBuy(state))
                                                                       .collect(Collectors.toList());
                        final Stream<List<Store.Item>> allCombinationsOfItems =
                                StreamSupport.stream(((Iterable<List<Store.Item>>) () -> new Iterator<List<Store.Item>>() {
                                    private long combinationBitFlag = 1;

                                    @Override
                                    public boolean hasNext() {
                                        return combinationBitFlag < 1 << itemsMayBy.size();
                                    }

                                    @Override
                                    public List<Store.Item> next() {
                                        long combinationBitFlag2 = combinationBitFlag;
                                        int itemIndex = 0;
                                        final List<Store.Item> itemsToBuy = new ArrayList<>(itemsMayBy.size());
                                        do {
                                            if ((combinationBitFlag2 & 1) == 1)
                                                itemsToBuy.add(itemsMayBy.get(itemIndex));
                                            combinationBitFlag2 >>= 1;
                                            ++itemIndex;
                                        } while (combinationBitFlag2 != 0);
                                        ++combinationBitFlag;
                                        return itemsToBuy;
                                    }
                                }).spliterator(), false);
                        return allCombinationsOfItems.map(storeItemsToBuy -> {
                            final BitSet itemsToBuy = new BitSet(items.size());
                            storeItemsToBuy.stream().mapToInt(storeItem -> storeItem.item.index).forEach(itemsToBuy::set);
                            final BitSet itemsBought = (BitSet) state.itemsBought.clone();
                            itemsBought.or(itemsToBuy);
                            final BitSet itemsToBuy2 = (BitSet) state.itemsToBuy.clone();
                            itemsToBuy2.andNot(itemsToBuy);

                            final int itemsPrice = storeItemsToBuy.stream().mapToInt(item -> item.price).sum();
                            final boolean isGoingHome = itemsToBuy2.isEmpty() ||
                                                        storeItemsToBuy.stream().anyMatch(Store.Item::isPerishable);
                            final double cost = itemsPrice + priceOfGasToStore + (isGoingHome ? priceOfGasToHome : 0);
                            final Action action = () -> cost;

                            final State state2 = new State(storesVisited,
                                                           itemsBought,
                                                           itemsToBuy2,
                                                           isGoingHome ? home : store,
                                                           state.spend + cost);
                            return new ActionStatePair<>(action, state2);
                        });
                    }).flatMap(Function.identity()).collect(Collectors.toList());
                }
            }).stream().mapToDouble(Action::cost).sum();
            System.out.println("States explored: " + search.statesExplored());
            return minimumSpend;
        }

        private static class Item implements Comparable<Item> {
            private final String  name;
            private final boolean perishable;
            private final int     index;
            private short minPrice = Short.MAX_VALUE;

            private Item(final String s, final int index) {
                this(s.replaceFirst("!$", ""), s.endsWith("!"), index);
            }

            private Item(final String name, final boolean perishable, final int index) {
                this.name = name;
                this.perishable = perishable;
                this.index = index;
            }

            private short getMinPrice() {
                return minPrice;
            }

            private void setMinPrice(short price) {
                if (price < minPrice)
                    minPrice = price;
            }

            @Override
            public int compareTo(final Item o) {
                return name.compareTo(o.name);
            }
        }

        private class Store {
            private final short      xPos;
            private final short      yPos;
            private final List<Item> items;
            private final double[]   goStorePrices; // last one reserved for home

            private Store(final String s, final List<Store> stores) {
                final String[] store = s.split(" ", 3);
                xPos = Short.parseShort(store[0]);
                yPos = Short.parseShort(store[1]);
                if (store[2].isEmpty())
                    items = Collections.emptyList();
                else
                    items = Arrays.stream(store[2].split(" ")).map(Item::new).collect(Collectors.toList());
                goStorePrices = new double[numStores + 1];
                for (int s2 = 0; s2 < stores.size(); s2++) {
                    Store store2 = stores.get(s2);
                    final double goStorePrice = Math.hypot(store2.xPos - xPos, store2.yPos - yPos) * priceOfGas;
                    store2.goStorePrices[stores.size()] = goStorePrice;
                    goStorePrices[s2] = goStorePrice;
                }
            }

            private double goStorePrice(final Store store2) {
                return goStorePrices[stores.indexOf(store2)];
            }

            private class Item {
                private final Case.Item item;
                private final short     price;

                private Item(final String s) {
                    this(s.split(":"));
                }

                private Item(final String[] strings) {
                    this(strings[0], Short.parseShort(strings[1]));
                }

                private Item(final String name, final short price) {
                    this(Case.this.items.stream().filter(item -> item.name.equals(name)).findAny().get(), price);
                }

                private Item(final Case.Item item, final short price) {
                    this.item = item;
                    this.price = price;
                    item.setMinPrice(price);
                }

                private boolean isToBuy(final State state) {
                    return state.itemsToBuy.get(item.index);
                }

                private boolean isPerishable() {
                    return item.perishable;
                }
            }
        }

        private class State {
            private final List<Store> storesVisited;
            private final BitSet      itemsBought;
            private final BitSet      itemsToBuy;
            private final Store       store;
            private final double      spend;

            private State(final List<Store> storesVisited,
                          final BitSet itemsBought,
                          final BitSet itemsToBuy,
                          final Store store, final double spend) {
                this.storesVisited = storesVisited;
                this.itemsBought = itemsBought;
                this.itemsToBuy = itemsToBuy;
                this.store = store;
                this.spend = spend;
            }

            @Override
            public int hashCode() {
                return Objects.hash(itemsBought, itemsToBuy, store);
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                final State state = (State) o;
                return Objects.equals(store, state.store) &&
                       Objects.equals(itemsBought, state.itemsBought) &&
                       Objects.equals(itemsToBuy, state.itemsToBuy);
            }
        }
    }
}
