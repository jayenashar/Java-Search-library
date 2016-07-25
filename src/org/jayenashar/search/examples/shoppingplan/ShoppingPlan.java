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
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        private final BitSet      perishableItems;
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
            perishableItems = new BitSet(items.size());
            for (int i = 0; i < itemsArray.length; i++) {
                final Item item = new Item(itemsArray[i], i);
                items.add(item);
                perishableItems.set(i, item.perishable);
            }
            assert numItems == items.size();

            stores = new ArrayList<>(numStores + 1);
            for (int storeIndex = 0; storeIndex < numStores; storeIndex++)
                stores.add(new Store(reader.readLine(), stores));
            home = new Store("0 0 ", stores);
            stores.add(home);

        }

        private double minimumSpend() {
            // case 1: 57289
            final AStarSearch<State> search = new AStarSearch<>(state -> {
                final int itemsMinPrice = state.itemsToBuy.stream().map(index -> items.get(index).getMinPrice()).sum();
                final double goHomePrice = state.store.goStorePrice(home);
                return itemsMinPrice + goHomePrice;
            });
//            // case 1: 74845
//            final AStarSearch<State> search = new AStarSearch<>(state -> {
//                final int itemsMinPrice = state.itemsToBuy.stream().map(index -> items.get(index).getMinPrice()).sum();
//                return itemsMinPrice;
//            });
//            // case 1: 80239
//            final AStarSearch<State> search = new AStarSearch<>(state -> {
//                final int itemsMinPrice = state.itemsToBuy.stream().map(index -> items.get(index).getMinPrice()).sum();
//                final double goNearestStoreAndHomePrice = stores.stream().filter(store -> store != home)
//                                                                .mapToDouble(store -> state.store.goStorePrice(store) +
//                                                                                      store.goStorePrice(home)).min()
//                                                                .orElse(Double.POSITIVE_INFINITY);
//                return itemsMinPrice + goNearestStoreAndHomePrice;
//            });
//            // case 1: 83230
//            final AStarSearch<State> search = new AStarSearch<>(state -> {
//                final double goHomePrice = state.store.goStorePrice(home);
//                return goHomePrice;
//            });
//            // case 1: 83381
//            final AStarSearch<State> search = new AStarSearch<>(state -> 0);
//            // case 1: 83417
//            final AStarSearch<State> search = new AStarSearch<>(state -> {
//                final int itemsMinPrice = state.itemsToBuy.stream().map(index -> items.get(index).getMinPrice()).sum();
//                final double goNearestStoreAndHomePrice = stores.stream().filter(store -> store.items.stream()
//                                                                                                     .anyMatch(storeItem -> state.itemsToBuy
//                                                                                                             .get(storeItem.item.index)))
//                                                                .mapToDouble(store -> state.store.goStorePrice(store) +
//                                                                                      store.goStorePrice(home)).min()
//                                                                .orElse(Double.POSITIVE_INFINITY);
//                return itemsMinPrice + goNearestStoreAndHomePrice;
//            });
//            // case 1: 83417
//            final AStarSearch<State> search = new AStarSearch<>(state -> {
//                final int itemsMinPrice = state.itemsToBuy.stream().map(index -> items.get(index).getMinPrice()).sum();
//                final double goNearestStoreAndHomePrice = stores.stream().filter(store -> state.hasPerishable ?
//                                                                                          store == home :
//                                                                                          store.items.stream()
//                                                                                                     .anyMatch(storeItem -> state.itemsToBuy
//                                                                                                             .get(storeItem.item.index)))
//                                                                .mapToDouble(store -> state.store.goStorePrice(store) +
//                                                                                      store.goStorePrice(home)).min()
//                                                                .orElse(Double.POSITIVE_INFINITY);
//                return itemsMinPrice + goNearestStoreAndHomePrice;
//            });
            final double minimumSpend = search.search(new StateSpaceSearchProblem<State>() {
                @Override
                public Iterable<State> initialStates() {
                    final BitSet itemsToBuy = new BitSet(items.size());
                    itemsToBuy.set(0, items.size());
                    return Collections.singleton(new State(Collections.emptyList(),
                                                           itemsToBuy,
                                                           home,
                                                           0,
                                                           false,
                                                           null));
                }

                @Override
                public boolean isGoal(final State state) {
                    return state.itemsToBuy.isEmpty();
                }

                @Override
                public Iterable<ActionStatePair<State>> successor(final State state) {
                    final Stream<Store> stores;
                    if (state.hasPerishable)
                        stores = Stream.of(state.store, home);
                    else
                        stores = Case.this.stores.stream().filter(store -> (!state.storesVisited.contains(store) ||
                                                                            state.store == store)
                                                                           && store != home);
                    return stores.map(store -> {
                        if (store == home) {
                            assert state.hasPerishable : "i should have forced myself home if there was another reason to go home" +
                                                         " without a perishable (e.g. i have all i need)";
                            final double cost = state.store.goStorePrice(home);
                            final Action action = () -> cost;
                            final State state2 = new State(state.storesVisited,
                                                           state.itemsToBuy,
                                                           home,
                                                           state.spend + cost,
                                                           false,
                                                           null);
                            return Stream.of(new ActionStatePair<>(action, state2));
                        }
                        // calculate these once per store, even if i may not go home
                        final double priceOfGasToStore = state.store.goStorePrice(store);
                        final List<Store> storesVisited;
                        assert (state.store == store) == state.storesVisited.contains(store);
                        assert state.store == home || state.storesVisited.contains(state.store);
                        assert !state.hasPerishable || state.store == store : "i have a perishable and should not change stores";
                        if (store != state.store) {
                            storesVisited = new ArrayList<>(state.storesVisited.size() + 1);
                            storesVisited.addAll(state.storesVisited);
                            storesVisited.add(store);
                        } else {
                            storesVisited = state.storesVisited;
                        }
                        assert storesVisited.contains(store);

                        final int fromIndex;
                        if (state.lastBought == null || state.store != store) {
                            fromIndex = 0;
                        } else {
                            fromIndex = store.items.indexOf(state.lastBought) + 1;
                            assert fromIndex > 0;
                        }
                        final List<Store.Item> itemsMayBy = store.items.subList(
                                fromIndex,
                                store.items.size()).stream().filter(storeItem -> storeItem.isToBuy(state)).collect(Collectors
                                                                                                                           .toList());
                        return itemsMayBy.stream().map(storeItemToBuy -> {
                            final BitSet itemsToBuy2 = (BitSet) state.itemsToBuy.clone();
                            itemsToBuy2.clear(storeItemToBuy.item.index);
                            final boolean hasPerishable = state.hasPerishable || storeItemToBuy.item.perishable;

                            final boolean isGoingHome = itemsToBuy2.isEmpty() || itemsMayBy.size() == 1 && hasPerishable;
                            final double cost =
                                    storeItemToBuy.price + priceOfGasToStore + (isGoingHome ? store.goStorePrice(home) : 0);
                            final Action action = () -> cost;

                            final State state2 = new State(storesVisited,
                                                           itemsToBuy2,
                                                           isGoingHome ? home : store,
                                                           state.spend + cost,
                                                           hasPerishable && !isGoingHome,
                                                           isGoingHome ? null : storeItemToBuy);
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
            private final short[]    itemsPriceSums;
            private final BitSet[]   itemsCaseItems;

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
                itemsPriceSums = new short[1 << items.size()];
                itemsCaseItems = new BitSet[1 << items.size()];
                for (int i = 1; i < 1 << items.size(); i++) {
                    final int finalI = i;
                    itemsCaseItems[i] = new BitSet(Case.this.items.size());
                    BitSet.valueOf(new long[]{i}).stream().forEach(storeItemIndex -> {
                        final Item item = items.get(storeItemIndex);
                        itemsPriceSums[finalI] += item.price;
                        itemsCaseItems[finalI].set(item.item.index);
                    });
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
            }
        }

        private class State {
            private final List<Store> storesVisited;
            private final BitSet      itemsToBuy;
            private final Store       store;
            private final double      spend;
            private final boolean     hasPerishable;
            private final Store.Item  lastBought;

            private State(final List<Store> storesVisited,
                          final BitSet itemsToBuy,
                          final Store store,
                          final double spend,
                          final boolean hasPerishable,
                          final Store.Item lastBought) {
                this.storesVisited = storesVisited;
                this.itemsToBuy = itemsToBuy;
                this.store = store;
                this.spend = spend;
                this.hasPerishable = hasPerishable;
                this.lastBought = lastBought;
            }

            @Override
            public int hashCode() {
                return Objects.hash(itemsToBuy, store, hasPerishable);
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                final State state = (State) o;
                return Objects.equals(itemsToBuy, state.itemsToBuy) &&
                       Objects.equals(store, state.store) &&
                       Objects.equals(hasPerishable, state.hasPerishable);
            }
        }
    }
}
